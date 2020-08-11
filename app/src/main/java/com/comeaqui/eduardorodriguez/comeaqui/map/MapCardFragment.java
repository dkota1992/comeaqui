package com.comeaqui.eduardorodriguez.comeaqui.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.behaviors.DragDownHideBehavior;
import com.comeaqui.eduardorodriguez.comeaqui.general.FoodLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.FoodElementFragment;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.comeaqui.eduardorodriguez.comeaqui.server.Server;

import com.comeaqui.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.RatingFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapCardFragment extends Fragment implements DragDownHideBehavior.OnBehaviorListener {

    TextView posterNameView;
    TextView posterUserName;
    ImageView posterImageView;
    ImageView starView;
    CardView cardView;
    FoodPost foodPost;

    FoodElementFragment fEFragment;

    boolean validPress = true;

    int favouriteId;

    private OnFragmentInteractionListener mListener;

    public MapCardFragment() {}

    public static MapCardFragment newInstance() {
        return new MapCardFragment();
    }

    public void showPost(FoodPost foodPost){
        this.foodPost = foodPost;
        setView();
    }

    ArrayList<AsyncTask> tasks = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_card, container, false);

        posterNameView = view.findViewById(R.id.poster_name);
        posterUserName = view.findViewById(R.id.meal);
        starView = view.findViewById(R.id.star);
        posterImageView = view.findViewById(R.id.poster_image);
        cardView = view.findViewById(R.id.map_card);

        DragDownHideBehavior.setListener(this);
        // setCardMovement();
        return view;
    }

    public void moveCardUp(boolean up){
        int move = cardView.getMeasuredHeight() + ((CoordinatorLayout.LayoutParams) cardView.getLayoutParams()).bottomMargin * 2;
        if (up) {
            cardView.setVisibility(View.VISIBLE);
            cardView.setTranslationY(move);
            cardView.animate().translationY(0).setDuration(move / 2);
        } else {
            cardView.animate().translationY(move).setDuration(move / 2);
        }
    }

    void setView(){
        fEFragment = FoodElementFragment.newInstance(foodPost);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container2, fEFragment)
                .commit();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.profile_rating, RatingFragment.newInstance(foodPost.owner.rating, foodPost.owner.ratingN))
                .commit();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.image_list, HorizontalImageDisplayFragment.newInstance(foodPost.id, 0, 4, 100, 0, 0))
                .commit();

        setClickLogic();

        favouriteId = foodPost.favouriteId;

        posterNameView.setText(foodPost.owner.first_name + " " + foodPost.owner.last_name);
        posterUserName.setText(foodPost.owner.username);
        starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);
        MapFragment.setMarkerDesign(MapFragment.markerHashMap.get(foodPost.id), true);

        if(!foodPost.owner.profile_photo.contains("no-image")) Glide.with(getContext()).load(foodPost.owner.profile_photo).into(posterImageView);

        posterImageView.setOnClickListener(v -> goToProfileView());
        setFavourite();
    }

    void goToProfileView(){
        Intent k = new Intent(getContext(), ProfileViewActivity.class);
        k.putExtra("userId", foodPost.owner.id);
        startActivity(k);
    }

    void setClickLogic(){
        cardView.setOnClickListener( v -> {
            if (validPress) {
                fEFragment.showProgress(true);
                Intent foodLook = new Intent(getContext(), FoodLookActivity.class);
                foodLook.putExtra("foodPostId", foodPost.id);
                getContext().startActivity(foodLook);
            }
        });
        cardView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    validPress = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    validPress = false;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }
            return false;
        });
    }


    void setFavourite(){
        starView.setOnClickListener(v -> {
            starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);
            MapFragment.setMarkerDesign(MapFragment.markerHashMap.get(foodPost.id), true);
            PostAsyncTask putFavourite = new PostAsyncTask(getResources().getString(R.string.server) + "/favourites/");
            tasks.add(putFavourite.execute(new String[]{"food_post_id", "" + foodPost.id}));

            foodPost.favourite = !foodPost.favourite;
            starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);

            MapFragment.setMarkerDesign(MapFragment.markerHashMap.get(foodPost.id), true);

        });
    }

    @Override
    public void onCloseBehavior() {
        mListener.onCardClosed();
    }

    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
        String uri;

        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getContext(), "POST", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                foodPost.favourite = new JsonParser().parse(response).getAsJsonObject().get("favourite").getAsBoolean();
            }
            super.onPostExecute(response);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onCardClosed();
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
