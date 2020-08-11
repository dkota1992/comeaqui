package com.comeaqui.eduardorodriguez.comeaqui.profile.post_and_reviews;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPostReview;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class PostAndReviewsFragment extends Fragment {
    private static final String USER_ID = "userId";
    private int userId;
    private static MyPostAndReviewsRecyclerViewAdapter adapter;

    RecyclerView recyclerView;
    LinearLayout noPosts;
    ArrayList<FoodPostReview> foodPostReviews = new ArrayList<>();
    ArrayList<AsyncTask> tasks = new ArrayList<>();
    ProgressBar loadingFoodsProgress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostAndReviewsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PostAndReviewsFragment newInstance(int user) {
        PostAndReviewsFragment fragment = new PostAndReviewsFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, user);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_postandreviews_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_user_postandreviews);
        noPosts = view.findViewById(R.id.no_posts);
        loadingFoodsProgress = view.findViewById(R.id.post_loading_progress);

        adapter = new MyPostAndReviewsRecyclerViewAdapter(foodPostReviews);
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
                        loadMorePosts();
                    }
                }
            }
        });

        getPostFromUser();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        getPostFromUser();
    }

    int page = 1;
    void getPostFromUser(){
        foodPostReviews = new ArrayList<>();
        page = 1;
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/user_food_posts_reviews/" + userId + "/" + page + "/").execute());
    }

    void loadMorePosts(){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/user_food_posts_reviews/" + userId + "/" + page + "/").execute());
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
                for (JsonElement pa : new JsonParser().parse(response).getAsJsonArray()) {
                    JsonObject jo = pa.getAsJsonObject();
                    FoodPostReview foodPost = new FoodPostReview(jo);
                    foodPostReviews.add(foodPost);
                }
                if (foodPostReviews.size() > 0){
                    noPosts.setVisibility(View.GONE);
                } else {
                    noPosts.setVisibility(View.VISIBLE);
                }
                adapter.addData(foodPostReviews);
                page++;
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
