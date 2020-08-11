package com.comeaqui.eduardorodriguez.comeaqui.review;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class ReviewGuestActivity extends AppCompatActivity implements StarReasonFragment.OnFragmentInteractionListener{


    Button submitButton;
    ScrollView scrollView;
    View progress;

    OrderObject orderObject;

    int rating;
    boolean[] reasonB;
    String review = "";
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guests_review);

        ImageView guestImage = findViewById(R.id.guest_image);
        TextView guestName = findViewById(R.id.guest_name);
        TextView rating = findViewById(R.id.rating);
        submitButton = findViewById(R.id.submitButton);
        scrollView = findViewById(R.id.scrollv);
        progress = findViewById(R.id.review_submit_progress);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("order") != null) {
            orderObject = (OrderObject) b.get("order");

            if(!orderObject.owner.profile_photo.contains("no-image")) {
                Glide.with(this).load(orderObject.owner.profile_photo).into(guestImage);
            }
            guestName.setText(orderObject.owner.first_name);
            rating.setText(String.format("%.01f", orderObject.owner.rating));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.star_rating_fr, StarReasonFragment.newInstance())
                    .commit();
        }

        setSubmitButton();
    }

    void submit(){
        tasks.add(new PostAsyncTask(getResources().getString(R.string.server) + "/rate_user/").execute(
                new String[]{"order_id", "" + orderObject.id},
                new String[]{"review", review},
                new String[]{"rating", "" + rating},
                new String[]{"star_reason", ""}
        ));
    }
    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
        String uri;

        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            submitButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), "POST", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                OrderObject orderObject = new OrderObject(jo);
                finish();
            }
            super.onPostExecute(response);
        }
    }


    void setSubmitButton(){
        submitButton.setAlpha(0.5f);
        submitButton.setOnClickListener(v -> {
            this.submit();
        });
    }

    @Override
    public void onFragmentInteraction(int rating, boolean[] reasonB, String review) {
        submitButton.setAlpha(1);
        this.rating = rating;
        this.reasonB = reasonB;
        this.review = review;
    }

    @Override
    public void onHasToScrollIfNeeded() {
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onRating(int rating) {
        this.rating = rating;
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
