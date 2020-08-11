package com.comeaqui.eduardorodriguez.comeaqui.utilities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPostImageObject;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.image_view_pager.ImagePagerActivity;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class HorizontalImageDisplayFragment extends Fragment {
    private static final String FOOD_POST_ID = "foodPostId";
    private static final String PADDING = "padding";
    private static final String GAP = "gap";
    private static final String HEIGHT = "height";
    private static final String RADIUS = "radius";
    private static final String ELEVATION = "radius";
    private int padding;
    private int gap;
    private int height;
    private int radius;
    private int elevation;

    private int foodPostId;
    private OnFragmentInteractionListener mListener;

    DisplayMetrics displayMetrics;

    ArrayList<FoodPostImageObject> foodPostImageObjects;

    LinearLayout imageList;
    ConstraintLayout parentView;
    HorizontalScrollView hScrollView;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public HorizontalImageDisplayFragment() {}
    public static HorizontalImageDisplayFragment newInstance(int foodPostId, int padding, int gap, int height, int radius, int elevation) {
        HorizontalImageDisplayFragment fragment = new HorizontalImageDisplayFragment();
        Bundle args = new Bundle();
        args.putInt(FOOD_POST_ID, foodPostId);
        args.putInt(PADDING, padding);
        args.putInt(GAP, gap);
        args.putInt(HEIGHT, height);
        args.putInt(RADIUS, radius);
        args.putInt(ELEVATION, elevation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            displayMetrics = getContext().getResources().getDisplayMetrics();
            foodPostId = getArguments().getInt(FOOD_POST_ID);
            padding = dpToPx(getArguments().getInt(PADDING));
            gap = dpToPx(getArguments().getInt(GAP));
            height = dpToPx(getArguments().getInt(HEIGHT));
            radius = dpToPx(getArguments().getInt(RADIUS));
            elevation = dpToPx(getArguments().getInt(ELEVATION));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFoodPostImages();
    }

    public int dpToPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizontal_image_display, container, false);
        imageList = view.findViewById(R.id.images_list);
        parentView = view.findViewById(R.id.parent_view);
        hScrollView = view.findViewById(R.id.h_scroll_view);
        return view;
    }
    void setImages(){
        imageList.removeAllViews();
        for (int i = 0; i < foodPostImageObjects.size(); i++){
            FoodPostImageObject io = foodPostImageObjects.get(i);
            CardView card = createCard(i);
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            card.addView(imageView);
            imageList.addView(card);

            final int fi = i;
            if(!io.image.contains("no-image")) {
                Glide.with(getContext()).load(io.image).into(imageView);
                imageView.setOnClickListener(v -> {
                    goToImageLook(fi);
                });
            }
        }
    }

    CardView createCard(int i){
        CardView card = new CardView(Objects.requireNonNull(getContext()));
        LinearLayout.LayoutParams lp;
        lp = new LinearLayout.LayoutParams(foodPostImageObjects.size() > 1 ? (int) (parentView.getMeasuredWidth() * 0.8) : parentView.getMeasuredWidth() - padding * 2, height);
        if (i == 0){
            lp.setMargins(padding, elevation, 0, elevation*2);
        } else {
            lp.setMargins(gap, elevation, 0, elevation*2);
        }
        if (i == foodPostImageObjects.size() - 1){
            if (foodPostImageObjects.size() > 1){
                lp.setMargins(gap, elevation, padding, elevation*2);
            } else {
                // lp.setMargins(0, elevation, padding, elevation*2);
            }
        }
        card.setLayoutParams(lp);
        card.setRadius(radius);
        card.setElevation(elevation);
        return card;
    }

    void goToImageLook(int i){
        Intent k = new Intent(getContext(), ImagePagerActivity.class);
        ArrayList<String> urls = new ArrayList<>();
        for(FoodPostImageObject fio: foodPostImageObjects){
            urls.add(fio.image);
        }
        k.putExtra("image_urls", urls);
        k.putExtra("index", i);
        startActivity(k);
    }

    void getFoodPostImages(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/food_images/" + foodPostId + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
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
                foodPostImageObjects = new ArrayList<>();
                for (JsonElement je: new JsonParser().parse(response).getAsJsonArray()){
                    foodPostImageObjects.add(new FoodPostImageObject(je.getAsJsonObject()));
                }
                setImages();
            }
            super.onPostExecute(response);
        }
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
