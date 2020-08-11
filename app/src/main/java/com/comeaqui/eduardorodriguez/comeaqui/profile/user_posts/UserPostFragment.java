package com.comeaqui.eduardorodriguez.comeaqui.profile.user_posts;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class UserPostFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String USER_ID = "userId";
    // TODO: Customize parameters
    private int userId;

    private static ArrayList<FoodPost> foodPosts;
    private static MyUserPostRecyclerViewAdapter adapter;


    RecyclerView recyclerView;
    LinearLayout noPosts;
    ProgressBar loadingFoodsProgress;


    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public UserPostFragment() {
    }


    void makeList(JsonArray jsonArray){
        for (JsonElement pa : jsonArray) {
            JsonObject jo = pa.getAsJsonObject();
            FoodPost foodPost = new FoodPost(jo);
            foodPosts.add(foodPost);
        }
        if (foodPosts.size() > 0){
            noPosts.setVisibility(View.GONE);
        } else {
            noPosts.setVisibility(View.VISIBLE);
        }
        adapter.addNewRow(foodPosts);
    }

    public static UserPostFragment newInstance(int userId) {
        UserPostFragment fragment = new UserPostFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        getPostFromUser(userId);
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userpost_list, container, false);

        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.recycler_user_post);
        noPosts = view.findViewById(R.id.no_posts);
        loadingFoodsProgress = view.findViewById(R.id.post_loading_progress);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyUserPostRecyclerViewAdapter(foodPosts);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isRecyclerScrollable(RecyclerView recyclerView) {
                return recyclerView.computeHorizontalScrollRange() > recyclerView.getWidth() || recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isRecyclerScrollable(recyclerView)){
                    if (!recyclerView.canScrollVertically(1)) {
                        loadMorePosts(userId);
                    }
                }
            }
        });
        return view;
    }

    int page = 1;
    void getPostFromUser(int userId){
        page = 1;
        foodPosts = new ArrayList<>();
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/user_food_posts/" + userId + "/" + page + "/").execute());
    }
    void loadMorePosts(int userId){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/user_food_posts/" + userId + "/" + page + "/").execute());
    }

    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            loadingFoodsProgress.setVisibility(View.VISIBLE);
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
            if (response != null){
                makeList(new JsonParser().parse(response).getAsJsonArray());
                page++;
                super.onPostExecute(response);
            }
            loadingFoodsProgress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        super.onDestroy();
    }
}
