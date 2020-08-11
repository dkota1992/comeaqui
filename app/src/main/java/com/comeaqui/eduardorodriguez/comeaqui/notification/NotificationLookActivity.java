package com.comeaqui.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.*;
import com.comeaqui.eduardorodriguez.comeaqui.general.StaticMapFragment;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.ProfileViewActivity;


import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.message_fragments.TwoOptionsMessageFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class NotificationLookActivity extends AppCompatActivity implements TwoOptionsMessageFragment.OnFragmentInteractionListener{

    Context context;

    TextView plusGuests;
    TextView plateNameView;
    TextView descriptionView;
    TextView priceView;
    TextView orderAction;
    TextView timeView;
    TextView date;
    TextView usernameView;
    TextView posterNameView;
    TextView posterLocationView;
    TextView statucMessage;
    TextView rating;
    Button confirmButton;
    Button rejectButton;
    LinearLayout buttons;

    ImageView dinnerImage;
    ImageView backView;
    View confirmNotificationProgress;
    FrameLayout waitingFrame;

    OrderObject orderObject;
    boolean confirm;

    TwoOptionsMessageFragment confirmFragment;
    TwoOptionsMessageFragment rejectFragment;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_look);
        context = getApplicationContext();

        plusGuests = findViewById(R.id.plus_guests);
        plateNameView = findViewById(R.id.postPlateName);
        descriptionView = findViewById(R.id.post_description);
        priceView = findViewById(R.id.price);
        date = findViewById(R.id.date);
        timeView = findViewById(R.id.time);
        confirmButton = findViewById(R.id.placeOrderButton);
        rejectButton = findViewById(R.id.rejectButton);
        usernameView = findViewById(R.id.username);
        posterNameView = findViewById(R.id.dinner_name);
        posterLocationView = findViewById(R.id.posterLocation);
        statucMessage = findViewById(R.id.status_message);
        rating = findViewById(R.id.rating);
        orderAction = findViewById(R.id.order_action);
        buttons = findViewById(R.id.buttons);

        dinnerImage = findViewById(R.id.dinner_image);
        backView = findViewById(R.id.back);
        confirmNotificationProgress = findViewById(R.id.confirm_notification_progress);
        waitingFrame = findViewById(R.id.waiting_frame);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.waiting_frame, WaitFragment.newInstance())
                .commit();

        confirmFragment = TwoOptionsMessageFragment.newInstance("Confirm Meal", "Are you sure you want to confirm this meal?", "CANCEL", "CONFIRM", false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.confirm_message, confirmFragment)
                .commit();

        rejectFragment = TwoOptionsMessageFragment.newInstance("Reject Meal", "Are you sure you want to reject this meal?", "CANCEL", "Reject", true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reject_message, rejectFragment)
                .commit();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("orderId") != null){
            int orderId = b.getInt("orderId");
            getOrderObject(orderId);
        }
        backView.setOnClickListener(v -> finish());
    }

    void getOrderObject(int orderId){
        tasks.add(new GetAsyncTask(context.getResources().getString(R.string.server) + "/order_detail/" + orderId + "/").execute());
    }

    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
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
                return ServerAPI.get(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                orderObject = new OrderObject(new JsonParser().parse(response).getAsJsonObject());
                startWaitingFrame(false);
                setDetails();
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

    void setDetails(){
        User userToShow = orderObject.owner.id == USER.id  ? orderObject.poster : orderObject.owner;
        posterNameView.setText(userToShow.first_name + " " + userToShow.last_name);
        usernameView.setText(userToShow.username);
        dinnerImage.setOnClickListener(v -> {
            goToProfileView(userToShow.id);
        });
        if(!userToShow.profile_photo.contains("no-image")){
            Glide.with(this).load(userToShow.profile_photo).into(dinnerImage);
        }

        if (orderObject.additionalGuests > 0){
            plusGuests.setVisibility(View.VISIBLE);
            plusGuests.setText("+" + orderObject.additionalGuests);
        }
        plateNameView.setText(orderObject.post.plate_name);
        descriptionView.setText(orderObject.post.description);
        posterLocationView.setText(orderObject.post.formatted_address);
        priceView.setText(orderObject.price_to_show);
        date.setText(orderObject.post.time_to_show);
        timeView.setText(orderObject.post.time_range);
        rating.setText(String.format("%.01f", orderObject.poster.rating));

        switch (orderObject.status){
            case "PENDING":
                orderAction.setText("Wants to eat with you!");
                orderAction.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimary));
                break;
            case "CONFIRMED":
                orderAction.setText("Meal confirmed!");
                orderAction.setBackground(ContextCompat.getDrawable(this, R.color.success));
                break;
            case "REJECTED":
                orderAction.setText("Meal rejected");
                orderAction.setBackground(ContextCompat.getDrawable(this, R.color.canceled));
                break;
            case "CANCELED":
                orderAction.setText("Meal canceled");
                orderAction.setBackground(ContextCompat.getDrawable(this, R.color.canceled));
                break;
            case "FINISHED":
                orderAction.setText("Has eaten with you");
                orderAction.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimary));
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("type", orderObject.post.type);
        FoodTypeFragment fragment = new FoodTypeFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.types, fragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.image_list, HorizontalImageDisplayFragment.newInstance(orderObject.post.id,24, 8, 200,4, 4))
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.static_map_frame, StaticMapFragment.newInstance(orderObject.post.lat, orderObject.post.lng))
                .commit();
        setConfirmCancelButton();
    }

    void showProgress(boolean show){
        if (show){
            confirmNotificationProgress.setVisibility(View.VISIBLE);
            buttons.setVisibility(View.GONE);
        } else {
            confirmNotificationProgress.setVisibility(View.GONE);
            buttons.setVisibility(View.VISIBLE);
        }
    }

    void setConfirmCancelButton(){
        if (orderObject.status.equals("PENDING")){
            confirmButton.setOnClickListener(v -> {
                confirm = true;
                confirmFragment.show(true);
            });
            rejectButton.setOnClickListener(v -> {
                confirm = false;
                rejectFragment.show(true);
            });
        } else {
            buttons.setVisibility(View.GONE);
            statucMessage.setVisibility(View.VISIBLE);
        }
        statucMessage.setText(orderObject.status);
        if (orderObject.status.equals("CONFIRMED")){
            statucMessage.setTextColor(ContextCompat.getColor(this, R.color.confirm));
        } else if (orderObject.status.equals("CANCELED") || orderObject.status.equals("REJECTED")){
            statucMessage.setTextColor(ContextCompat.getColor(this, R.color.canceled));
        } else if (orderObject.status.equals("FINISHED")){
            statucMessage.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    void confirmOrder(OrderObject order, boolean confirm, Context context){
        PostAsyncTask orderStatus = new PostAsyncTask(context.getString(R.string.server) + "/set_order_status/");
        order.status = confirm ? "CONFIRMED" : "REJECTED";
        tasks.add(orderStatus.execute(
                new String[]{"order_id",  order.id + ""},
                new String[]{"order_status", order.status}
        ));
    }
    private static class PostAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    try{
                        OrderObject order = new OrderObject(jo);
                    } catch (Exception e){}
                } else {
                }
            }
            super.onPostExecute(response);
        }
    }

    void goToProfileView(int userId){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("userId", userId);
        startActivity(k);
    }

    @Override
    public void leftButtonPressed() {

    }

    @Override
    public void rightButtonPressed() {
        confirmOrder(orderObject, confirm, this);
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
