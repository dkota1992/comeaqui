package com.comeaqui.eduardorodriguez.comeaqui.map.add_food.add_images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.map.add_food.image_look.ImageLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPostImageObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.SavedFoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class AddImagesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ImageView[] imageViews;
    ImageView[] addImageViews;
    ProgressBar[] progressBars;
    LinkedList<FoodPostImageObject> imageObjects;

    FoodPost foodPost;

    int indexClicked;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public AddImagesFragment() {}

    public static AddImagesFragment newInstance() {
        return new AddImagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addImage(Uri uri){
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            uploadImage(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.foodPost != null) {
            initializeFoodPost(this.foodPost.id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_images, container, false);
        imageObjects = new LinkedList<>();
        imageViews = new ImageView[]{
                view.findViewById(R.id.image0),
                view.findViewById(R.id.image1),
                view.findViewById(R.id.image2)
        };
        addImageViews = new ImageView[]{
                view.findViewById(R.id.add_image_0),
                view.findViewById(R.id.add_image_1),
                view.findViewById(R.id.add_image_2)
        };
        progressBars = new ProgressBar[]{
                view.findViewById(R.id.progress0),
                view.findViewById(R.id.progress1),
                view.findViewById(R.id.progress2)
        };
        setImageAbailable();
        return view;
    }

    void setImageAbailable(){
        int i = 0;
        for (ImageView iv: imageViews){
            iv.setImageDrawable(null);
        }
        while (i < imageObjects.size()){
            final int finalI = i;
            imageViews[i].setOnClickListener(v -> {
                indexClicked = finalI;
                goToImageLook(imageObjects.get(indexClicked).id);
            });
            Glide.with(getContext()).load(imageObjects.get(i).image).into(imageViews[i]);
            i++;
        }
        if (i < imageViews.length) {
            final int finalI = i;
            imageViews[i].setOnClickListener(v ->{
                indexClicked = finalI;
                mListener.onAddImage();
            });
            addImageViews[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.add_image_icon));
        }
    }

    void goToImageLook(int imageId){
        Intent k = new Intent(getContext(), ImageLookActivity.class);
        k.putExtra("imageId", imageId);
        startActivity(k);
    }

    void showProgress(boolean show,int index){
        progressBars[index].setVisibility(show ? View.VISIBLE: View.GONE);
        addImageViews[index].setVisibility(show ? View.GONE: View.VISIBLE);
    }

    public void initializeFoodPost(int foodPostId){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostId + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
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
                foodPost = new SavedFoodPost(new JsonParser().parse(response).getAsJsonObject());
                imageObjects = new LinkedList<>();
                imageObjects.addAll(foodPost.images);
                setImageAbailable();
            }
            super.onPostExecute(response);
        }
    }

    public void uploadImage(Bitmap imageBitmap){
        PostImagesAsyncTask postI = new PostImagesAsyncTask(
                getResources().getString(R.string.server) + "/add_food_images/" + foodPost.id + "/",
                imageBitmap,
                indexClicked
        );
        tasks.add(postI.execute());
    }

    class PostImagesAsyncTask extends AsyncTask<String, Void, String> {
        String uri;
        Bitmap image;
        int index;
        public PostImagesAsyncTask(String uri, Bitmap imageBitmaps, int indexClicked){
            this.uri = uri;
            this.image = imageBitmaps;
            this.index = indexClicked;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true, index);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return ServerAPI.uploadImage(getContext(), "PATCH", this.uri, "image", image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                imageObjects.add(new FoodPostImageObject(new JsonParser().parse(response).getAsJsonObject()));
            }
            setImageAbailable();
            showProgress(false, index);
            super.onPostExecute(response);
        }
    }












    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void onAddImage();
    }
}
