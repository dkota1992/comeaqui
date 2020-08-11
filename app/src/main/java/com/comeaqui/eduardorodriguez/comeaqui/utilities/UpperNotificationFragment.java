package com.comeaqui.eduardorodriguez.comeaqui.utilities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.general.FoodLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;
import com.comeaqui.eduardorodriguez.comeaqui.order.OrderLookActivity;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;


public class UpperNotificationFragment extends Fragment {

    TextView title;
    TextView username;
    TextView plateName;
    TextView time;
    TextView time2;
    ImageView posterImage;
    CardView orderCard;
    CardView postCard;
    LinearLayout allCards;

    OrderObject orderObject;
    FoodPost nextFoodPost;
    float initialY, dY;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public UpperNotificationFragment() {}
    public static UpperNotificationFragment newInstance() {
        return new UpperNotificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upper_notification, container, false);

        title = view.findViewById(R.id.title);
        username = view.findViewById(R.id.meal);
        plateName = view.findViewById(R.id.plate_name);
        time = view.findViewById(R.id.time);
        time2 = view.findViewById(R.id.time2);
        posterImage = view.findViewById(R.id.poster_image);
        orderCard = view.findViewById(R.id.order_card);
        postCard = view.findViewById(R.id.post_card);
        allCards = view.findViewById(R.id.all_cards);


        setCardView(orderCard);
        setCardView(postCard);

        orderCard.setOnClickListener(v -> {
            Intent goToOrders = new Intent(getContext(), OrderLookActivity.class);
            goToOrders.putExtra("orderId", orderObject.id);
            startActivity(goToOrders);
        });

        postCard.setOnClickListener(v -> {
            Intent foodLook = new Intent(getContext(), FoodLookActivity.class);
            foodLook.putExtra("foodPostId", nextFoodPost.id);
            startActivity(foodLook);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getConfirmedOrders();
        getConfirmedFoodPosts();
    }

    public void refreshUpperNotifications(){
        getConfirmedOrders();
        getConfirmedFoodPosts();
    }

    void setOrderCard(){
        if (orderObject != null){
            orderCard.setVisibility(View.VISIBLE);
            title.setText("Meal with " +  orderObject.poster.first_name + orderObject.poster.last_name);
            username.setText(orderObject.poster.username);
            plateName.setText( orderObject.post.plate_name);
            time.setText(orderObject.post.time_to_show);
            if(!orderObject.poster.profile_photo.contains("no-image")) {
                Glide.with(getActivity()).load(orderObject.poster.profile_photo).into(posterImage);
            } else {
                posterImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.no_profile_photo));
            }
        } else {
            orderCard.setVisibility(View.GONE);
        }
    }

    void setNextFoodPost(){
        if (nextFoodPost != null){
            postCard.setVisibility(View.VISIBLE);
            time2.setText(nextFoodPost.time_to_show);
        } else {
            postCard.setVisibility(View.GONE);
        }
    }

    boolean result = false;
    void setCardView(View view){
        orderCard.setOnClickListener(v -> {
            Intent orderLook = new Intent(getContext(), OrderLookActivity.class);
            orderLook.putExtra("orderId", orderObject.id);
            orderLook.putExtra("delete", false);
            getContext().startActivity(orderLook);
        });
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dY = v.getY() - event.getRawY();
                    initialY = v.getY();
                    result = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getRawY() + dY < 0){
                        v.animate()
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                    }
                    result = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (initialY - v.getY() > v.getHeight() / 2){
                        v.animate()
                                .y(dY)
                                .setDuration(100).withEndAction(() -> {
                                    v.setVisibility(View.GONE);
                                })
                                .start();
                    } else {
                        v.animate()
                                .y(initialY)
                                .setDuration(100)
                                .start();
                    }
                    break;
            }
            return result;
        });
    }

    void getConfirmedOrders(){
        tasks.add(new GetConfirmedOrdersAsyncTask(getResources().getString(R.string.server) +  "/my_next_confirmed_order/").execute());
    }
    private class GetConfirmedOrdersAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetConfirmedOrdersAsyncTask(String uri){
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
                JsonArray ja = new JsonParser().parse(response).getAsJsonArray();
                if (ja.size() > 0){
                    orderObject = new OrderObject(ja.get(0).getAsJsonObject());
                    setOrderCard();
                }
            }
            super.onPostExecute(response);
        }

    }
    void getConfirmedFoodPosts(){
        tasks.add(new GetConfirmedPostsAsyncTask(getResources().getString(R.string.server) +  "/my_next_confirmed_post/").execute());
    }
    private class GetConfirmedPostsAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        GetConfirmedPostsAsyncTask(String uri){
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
                JsonArray ja = new JsonParser().parse(response).getAsJsonArray();
                if (ja.size() > 0){
                    nextFoodPost = new FoodPost(ja.get(0).getAsJsonObject());
                    setNextFoodPost();
                }
            }
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
