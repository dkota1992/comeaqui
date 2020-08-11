package com.comeaqui.eduardorodriguez.comeaqui.general.dinner_list;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.LinkedHashMap;

public class DinnerFragment extends Fragment {

    private static final String FOOD_POST_ID = "food_post_id";
    private int foodPostId;
    private OnListFragmentInteractionListener mListener;


    LinkedHashMap<Integer, OrderObject> data;
    MyDinnerRecyclerViewAdapter dinnerAdapter;

    FoodPostDetail foodPostDetail;
    RecyclerView recyclerView;
    FrameLayout waitingFrame;

    public DinnerFragment() {}

    public static DinnerFragment newInstance(int foodPostId) {
        DinnerFragment fragment = new DinnerFragment();
        Bundle args = new Bundle();
        args.putInt(FOOD_POST_ID, foodPostId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foodPostId = getArguments().getInt(FOOD_POST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dinner_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        waitingFrame = view.findViewById(R.id.waiting_frame);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.waiting_frame, WaitFragment.newInstance())
                .commit();
        getFoodPostDetailsAndSet(foodPostId);
        return view;
    }

    void getFoodPostDetailsAndSet(int foodPostId){
        new GetAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostId + "/"){
            @Override
            protected void onPostExecute(String response) {

                super.onPostExecute(response);
            }
        }.execute();
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            startWaitingFrame(true);
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
                foodPostDetail = new FoodPostDetail(new JsonParser().parse(response).getAsJsonObject());
                dinnerAdapter = new MyDinnerRecyclerViewAdapter(foodPostDetail.confirmedOrdersList, mListener);
                recyclerView.setAdapter(dinnerAdapter);
                startWaitingFrame(false);
            }
            startWaitingFrame(false);
            super.onPostExecute(response);
        }

    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitingFrame.setVisibility(View.VISIBLE);
        } else {
            waitingFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(OrderObject item);
        void onChatInteraction(OrderObject item);
    }
}
