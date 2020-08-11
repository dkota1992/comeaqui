package com.comeaqui.eduardorodriguez.comeaqui.order;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.objects.SavedFoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class HostingFragment extends Fragment {
    private OnListFragmentInteractionListener mListener;

    RecyclerView recyclerView;

    SwipeRefreshLayout pullToRefresh;
    LinkedHashMap<Integer, SavedFoodPost> data = new LinkedHashMap<>();
    MyHostingRecyclerViewAdapter hostingAdapter;
    WebSocketClient mWebSocketClient;
    LinearLayout noHosting;
    ProgressBar loadingProgress;

    View view;

    ArrayList<AsyncTask> tasks = new ArrayList<>();
    public HostingFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HostingFragment newInstance() {
        return new HostingFragment();
    }

    void makeList(JsonArray jsonArray){
        try {
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                SavedFoodPost oo = new SavedFoodPost(jo);
                data.put(oo.id, oo);
            }
            if (data.size() > 0){
                noHosting.setVisibility(View.GONE);
            } else {
                noHosting.setVisibility(View.VISIBLE);
            }
            hostingAdapter.addData(new ArrayList<>(data.values()));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hosting_list, container, false);
        recyclerView = view.findViewById(R.id.hosting_list);
        noHosting = view.findViewById(R.id.no_hosting);
        loadingProgress = view.findViewById(R.id.loading_progress);

        hostingAdapter = new MyHostingRecyclerViewAdapter(new ArrayList<>(data.values()), mListener);
        recyclerView.setAdapter(hostingAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isRecyclerScrollable(RecyclerView recyclerView) {
                return recyclerView.computeHorizontalScrollRange() > recyclerView.getWidth() || recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isRecyclerScrollable(recyclerView)){
                    if (!recyclerView.canScrollVertically(1)) {
                        loadMoreData();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataAndSet();
    }

    int page = 1;
    void getDataAndSet(){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        page = 1;
        data = new LinkedHashMap<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_hosting/" + page + "/").execute());
    }

    void loadMoreData(){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_hosting/"+ page + "/").execute());
    }

    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            loadingProgress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                makeList(new JsonParser().parse(response).getAsJsonArray());
                page++;
            }
            loadingProgress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (null != mWebSocketClient)
            mWebSocketClient.close();
    }

    @Override
    public void onDestroy() {
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        super.onDestroy();
    }

    public interface OnListFragmentInteractionListener {
        void goToPostLook(FoodPost item);
        void goToPostEdit(FoodPost item);
    }
}
