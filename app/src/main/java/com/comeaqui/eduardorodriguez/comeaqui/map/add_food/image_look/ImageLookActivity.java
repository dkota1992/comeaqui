package com.comeaqui.eduardorodriguez.comeaqui.map.add_food.image_look;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPostImageObject;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class ImageLookActivity extends AppCompatActivity {

    ImageView image;
    ImageButton options;

    FoodPostImageObject imageObject;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_look);
        image = findViewById(R.id.image);
        options = findViewById(R.id.options);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null && b.get("imageId") != null) {
            getFoodPostImage(b.getInt("imageId"));
        }
        setOptionsMenu();
        findViewById(R.id.close).setOnClickListener(v -> finish());
    }

    void setOptionsMenu(){
        options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, options);
            popupMenu.getMenu().add("Delete");

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getTitle().toString()){
                    case "Delete":
                        deleteImage(imageObject.id);
                }
                return true;
            });
            popupMenu.show();
        });
    }

    void deleteImage(int imageId){
        tasks.add(new DeleteImageAsyncTaks(getResources().getString(R.string.server) + "/image/" + imageId + "/").execute());
    }
    class DeleteImageAsyncTaks extends AsyncTask<String[], Void, String> {
        private String uri;
        public DeleteImageAsyncTaks(String uri){
            this.uri = uri;
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.delete(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                finish();
            }
            super.onPostExecute(response);
        }
    }

    void getFoodPostImage(int imageId) {
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/image/" + imageId + "/").execute());
    }

    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;

        public GetAsyncTask(String uri) {
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                imageObject = new FoodPostImageObject(new JsonParser().parse(response).getAsJsonObject());
                Glide.with(getApplication()).load(imageObject.image).into(image);
                options.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(response);
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
}