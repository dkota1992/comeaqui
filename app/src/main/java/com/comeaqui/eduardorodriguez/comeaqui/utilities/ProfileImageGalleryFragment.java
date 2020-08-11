package com.comeaqui.eduardorodriguez.comeaqui.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPostImageObject;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.image_view_pager.ImagePagerActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileImageGalleryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_ID = "userId";

    // TODO: Rename and change types of parameters
    private int userId;

    private OnFragmentInteractionListener mListener;
    int count;

    View view;
    LinearLayout imageListLayout;
    LinearLayout currentHorizontalLayout;
    LinearLayout noMedia;
    ProgressBar progressBar;
    NestedScrollView scrollView;

    DisplayMetrics displayMetrics;

    int IMAGE_BY_ROW = 3;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public ProfileImageGalleryFragment() {
        // Required empty public constructor
    }

    public static ProfileImageGalleryFragment newInstance(int userId) {
        ProfileImageGalleryFragment fragment = new ProfileImageGalleryFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        count = 0;
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
        view = inflater.inflate(R.layout.fragment_profile_image_gallery, container, false);
        noMedia = view.findViewById(R.id.no_media);
        progressBar = view.findViewById(R.id.progress);
        scrollView = view.findViewById(R.id.scrollView);

        count = 0;
        displayMetrics = view.getContext().getResources().getDisplayMetrics();
        initializeFirstLayout();
        getPostFromUser();

        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!scrollView.canScrollVertically(1)) {
                loadMoreImages();
            }
        });
        return view;
    }

    private void initializeFirstLayout(){
        imageListLayout = view.findViewById(R.id.image_list);

        LinearLayout horizontalLayout = new LinearLayout(getContext());
        horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                displayMetrics.widthPixels / IMAGE_BY_ROW));
        imageListLayout.addView(horizontalLayout);
        currentHorizontalLayout = horizontalLayout;
    }

    public int dpToPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    int page = 1;
    void getPostFromUser(){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        page = 1;
        GetAsyncTask process = new GetAsyncTask(getResources().getString(R.string.server) + "/user_images/" + userId + "/" + page + "/");
        tasks.add(process.execute());
    }

    void loadMoreImages(){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        GetAsyncTask process = new GetAsyncTask(getResources().getString(R.string.server) + "/user_images/" + userId + "/" + page + "/");
        tasks.add(process.execute());
    }
    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
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
                JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
                makeList(jsonArray);
                page++;
            }
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(response);
        }
    }

    void makeList(JsonArray jsonArray){
        ArrayList<String> imageUrls = new ArrayList<>();
        if (page == 1 && jsonArray.size() == 0){
            noMedia.setVisibility(View.VISIBLE);
        } else {
            noMedia.setVisibility(View.GONE);
        }
        for (int i = 0; i <jsonArray.size(); i++){
            JsonElement pa = jsonArray.get(i);
            JsonObject jo = pa.getAsJsonObject();
            String imageUrl = new FoodPostImageObject(jo).image;
            imageUrls.add(imageUrl);
            if (imageUrl != null && !imageUrl.contains("no-image")){
                final int fi = i;
                count++;
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        displayMetrics.widthPixels / IMAGE_BY_ROW,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                currentHorizontalLayout.addView(imageView);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                int padding = 5;
                imageView.setPadding(padding,padding,padding,padding);

                makeImageRoundCornered(imageView, 20);

                Glide.with(view.getContext()).load(imageUrl).into(imageView);

                imageView.setOnClickListener((v) -> {
                    Intent imageLook = new Intent(getContext(), ImagePagerActivity.class);
                    imageLook.putExtra("image_urls", imageUrls);
                    imageLook.putExtra("index", fi);
                    startActivity(imageLook);
                });

                if (count % IMAGE_BY_ROW == 0){
                    LinearLayout horizontalLayout = new LinearLayout(getContext());
                    horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            displayMetrics.widthPixels / IMAGE_BY_ROW));
                    horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                    imageListLayout.addView(horizontalLayout);
                    currentHorizontalLayout = horizontalLayout;
                }
            }
        }
    }

    private void makeImageRoundCornered(ImageView imageView, int curveRadius){
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), curveRadius);
            }
        });
        imageView.setClipToOutline(true);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onDestroy() {
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        super.onDestroy();
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
