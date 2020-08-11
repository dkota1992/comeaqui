package com.comeaqui.eduardorodriguez.comeaqui.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.BuildConfig;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.general.attend_message.AttendFragment;
import com.comeaqui.eduardorodriguez.comeaqui.general.dinner_list.DinnerListActivity;
import com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments.FoodCommentFragment;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.PaymentMethodObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment.PaymentMethodsActivity;


import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.message_fragments.TwoOptionsMessageFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class FoodLookActivity extends AppCompatActivity implements
        TwoOptionsMessageFragment.OnFragmentInteractionListener,
        AttendFragment.OnFragmentInteractionListener,
        FoodCommentFragment.OnFragmentInteractionListener{

    private static final int REQUEST_EDITED_FOODPOST = 1;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    boolean isCollapsed = true;

    TextView plateNameView;
    TextView descriptionView;
    TextView priceView;
    TextView date;
    TextView timeView;
    TextView usernameView;
    TextView posterNameView;
    TextView posterLocationView;
    TextView changePaymentMethod;
    TextView postStatus;
    TextView attendToComment;
    Button attendMealButton;

    ImageView posterImage;
    LinearLayout paymentMethod;
    LinearLayout profileLook;
    View progress;
    LinearLayout dinnersListView;
    ImageView[] dinnerArray;
    TextView[] plusArray;
    FrameLayout waitingFrame;
    Menu collapseMenu;
    LinearLayout pendingPaymentMethod;
    TextView pendingPaymentMethodText;
    TextView cardLastNumbers;
    ImageView cardIcon;
    NestedScrollView scrollView;

    TwoOptionsMessageFragment sureFragment;
    AttendFragment attendFragment;

    Integer fpId;
    FoodPostDetail foodPostDetail;
    String userStatusInPost = "";
    int additionalGuests;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    FoodCommentFragment foodCommentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_look);


        toolbar = findViewById(R.id.anim_toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBarLayout = findViewById(R.id.appbar);

        plateNameView = findViewById(R.id.postPlateName);
        descriptionView = findViewById(R.id.post_description);
        priceView = findViewById(R.id.price);
        attendMealButton = findViewById(R.id.placeOrderButton);
        timeView = findViewById(R.id.time);
        date = findViewById(R.id.date);
        usernameView = findViewById(R.id.username);
        posterNameView = findViewById(R.id.poster_name);
        posterLocationView = findViewById(R.id.posterLocation);

        posterImage = findViewById(R.id.poster_image);
        paymentMethod = findViewById(R.id.payment_method_layout);
        changePaymentMethod = findViewById(R.id.change_payment);
        progress = findViewById(R.id.place_order_progress);
        dinnersListView = findViewById(R.id.dinners_list_view);
        profileLook = findViewById(R.id.profile_look);
        pendingPaymentMethodText = findViewById(R.id.pending_payment_method_text);
        pendingPaymentMethod = findViewById(R.id.pending_payment_method);
        cardIcon = findViewById(R.id.card_icon);
        cardLastNumbers = findViewById(R.id.card_last_numbers);
        scrollView = findViewById(R.id.scrollableview);
        postStatus = findViewById(R.id.post_status);
        attendToComment = findViewById(R.id.attend_to_comment);


        waitingFrame = findViewById(R.id.waiting_frame);
        setToolbar();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("foodPostId") != null){
            fpId = b.getInt("foodPostId");
            if (fpId != null){
                getFoodPostDetailsAndSet(fpId);
            }
        }

        changePaymentMethod.setOnClickListener(v -> {
            Intent paymentMethod = new Intent(this, PaymentMethodsActivity.class);
            paymentMethod.putExtra("changeMode", true);
            startActivity(paymentMethod);
        });

        pendingPaymentMethodText.setOnClickListener(v -> {
            Intent paymentMethodA = new Intent(this, PaymentMethodsActivity.class);
            paymentMethodA.putExtra("changeMode", true);
            startActivity(paymentMethodA);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (USER.id != fpId) {
            getMyChosenCard();
        }
    }

    void setDetails(){
        posterNameView.setText(foodPostDetail.owner.first_name + " " + foodPostDetail.owner.last_name);
        usernameView.setText("@" + foodPostDetail.owner.username);
        plateNameView.setText(foodPostDetail.plate_name);
        descriptionView.setText(foodPostDetail.description);
        posterLocationView.setText(foodPostDetail.formatted_address);
        priceView.setText(foodPostDetail.price_to_show);
        timeView.setText(foodPostDetail.time_range);
        date.setText(foodPostDetail.time_to_show);

        Bundle bundle = new Bundle();
        bundle.putSerializable("type", foodPostDetail.type);
        FoodTypeFragment fragment = new FoodTypeFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.types, fragment)
                .commitAllowingStateLoss();


        if(!foodPostDetail.owner.profile_photo.contains("no-image")) {
            Glide.with(this).load(foodPostDetail.owner.profile_photo).into(posterImage);
            profileLook.setOnClickListener(v -> goToProfileView(foodPostDetail.owner));
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.static_map_frame, StaticMapFragment.newInstance(foodPostDetail.lat, foodPostDetail.lng))
                .commitAllowingStateLoss();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.image_list, HorizontalImageDisplayFragment.newInstance(foodPostDetail.id, 0, 4, 200,0, 0))
                .commitAllowingStateLoss();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.waiting_frame, WaitFragment.newInstance())
                .commitAllowingStateLoss();

        sureFragment = TwoOptionsMessageFragment.newInstance("Delete", "Are you sure you want to delete this post?", "CANCEL", "DELETE", true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sure_message, sureFragment)
                .commitAllowingStateLoss();

        attendFragment = AttendFragment.newInstance(foodPostDetail.dinners_left, foodPostDetail.price);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.attend_message, attendFragment)
                .commitAllowingStateLoss();


        findViewById(R.id.other).setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });

        setPlaceButton();
        setDinners();
    }

    void setStatus(String message, int color){
        postStatus.setVisibility(View.VISIBLE);
        postStatus.setText(message);
        postStatus.setBackground(ContextCompat.getDrawable(this, color));

        attendMealButton.setText(message);
        attendMealButton.setBackgroundColor(Color.TRANSPARENT);
        attendMealButton.setTextColor(ContextCompat.getColor(this, color));
    }

    void setPlaceButton(){
        if (foodPostDetail.owner.id != USER.id){
            if (userStatusInPost.equals("CONFIRMED")) {
                foodCommentFragment = FoodCommentFragment.newInstance(foodPostDetail.id, "post");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.foodpost_comments, foodCommentFragment)
                        .commitAllowingStateLoss();

                setStatus("Confirmed", R.color.success);
            } else if (userStatusInPost.equals("PENDING")) {
                setStatus("Pending", R.color.colorPrimary);
            } else if (userStatusInPost.equals("CANCELED")) {
                setStatus("Candeled", R.color.canceled);
            } else if (userStatusInPost.equals("REJECTED")) {
                setStatus("Rejected", R.color.canceled);
            } else if (userStatusInPost.equals("FINISHED")) {
                foodCommentFragment = FoodCommentFragment.newInstance(foodPostDetail.id, "post");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.foodpost_comments, foodCommentFragment)
                        .commitAllowingStateLoss();

                setStatus("Finished", R.color.colorPrimary);
            } else if (foodPostDetail.dinners_left == 0){
                setStatus("Finished", R.color.colorPrimary);
            } else {
                if (foodPostDetail.status.equals("OPEN")) {
                    attendMealButton.setOnClickListener(v -> {
                        attendFragment.show(true);
                    });
                    attendToComment.setOnClickListener(v -> {
                        attendFragment.show(true);
                    });
                    attendToComment.setVisibility(View.VISIBLE);
                } else if (foodPostDetail.status.equals("IN_COURSE")){
                    setStatus("Meal in course", R.color.colorPrimary);
                } else if(foodPostDetail.status.equals("FINISHED")){
                    setStatus("Meal finished", R.color.colorPrimary);
                }
            }
            attendMealButton.setVisibility(View.VISIBLE);
        } else {
            foodCommentFragment = FoodCommentFragment.newInstance(foodPostDetail.id, "post");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.foodpost_comments, foodCommentFragment)
                    .commitAllowingStateLoss();
        }
    }
    void setDinners(){

        dinnerArray = new ImageView[]{
                findViewById(R.id.dinner0),
                findViewById(R.id.dinner1),
                findViewById(R.id.dinner2),
                findViewById(R.id.dinner3),
                findViewById(R.id.dinner4),
                findViewById(R.id.dinner5),
                findViewById(R.id.dinner6),
        };
        plusArray = new TextView[]{
                findViewById(R.id.plus0),
                findViewById(R.id.plus1),
                findViewById(R.id.plus2),
                findViewById(R.id.plus3),
                findViewById(R.id.plus4),
                findViewById(R.id.plus5),
                findViewById(R.id.plus6),
        };
        TextView dinnersNumber = findViewById(R.id.dinners_number);

        dinnersListView.setOnClickListener((v) -> {
            if (foodPostDetail.confirmedOrdersList.size() > 0) {
                startActivity(new Intent(this, DinnerListActivity.class).putExtra("foodPostId", foodPostDetail.id));
            }
        });

        if (foodPostDetail.confirmedOrdersList.size() > 0){
            dinnersNumber.setText((foodPostDetail.max_dinners - foodPostDetail.dinners_left) + "/" + foodPostDetail.max_dinners + " dinners for this meal");
        } else {
            dinnersNumber.setText("No dinners for this meal yet");
        }
        int i = 0;
        while (i < dinnerArray.length && i < foodPostDetail.confirmedOrdersList.size()){
            if(!foodPostDetail.confirmedOrdersList.get(i).owner.profile_photo.contains("no-image")) {
                Glide.with(this).load(foodPostDetail.confirmedOrdersList.get(i).owner.profile_photo).into(dinnerArray[i]);
                if (foodPostDetail.confirmedOrdersList.get(i).additionalGuests > 0){
                    plusArray[i].setVisibility(View.VISIBLE);
                    plusArray[i].setText("+" + foodPostDetail.confirmedOrdersList.get(i).additionalGuests);
                }
            }
            dinnerArray[i].setVisibility(View.VISIBLE);
            i++;
        }
    }
    void setToolbar(){
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setTitle("Food Post");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorPrimary_trans));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.collapseMenu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        setCollapseLogic();
        return true;
    }

    int vo = 1;
    void setCollapseLogic(){
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(vo)-appBarLayout.getTotalScrollRange() == 0) {
                if (!isCollapsed){
                    isCollapsed = true;
                    final Drawable upArrow = getResources().getDrawable(R.drawable.back_arrow_white);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    findViewById(R.id.action_settings).setBackground(ContextCompat.getDrawable(this, R.drawable.collapse_three_dots));
                    findViewById(R.id.other).setBackground(ContextCompat.getDrawable(this, R.drawable.share_icon_collapsed));
                }
            } else {
                if (isCollapsed){
                    isCollapsed = false;
                    final Drawable upArrow = getResources().getDrawable(R.drawable.back_arrow_with_background);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    findViewById(R.id.action_settings).setBackground(ContextCompat.getDrawable(this, R.drawable.three_dots_with_background));
                    findViewById(R.id.other).setBackground(ContextCompat.getDrawable(this, R.drawable.share_icon));
                }
            }
            vo = verticalOffset;
            invalidateOptionsMenu();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_settings));
        if (foodPostDetail.owner.id == USER.id){
            popupMenu.getMenu().add("Edit");
            if (foodPostDetail.confirmedOrdersList.size() == 0 || (foodPostDetail.confirmedOrdersList.size() > 0 && foodPostDetail.confirmedOrdersList.get(0).status.equals("FINISHED"))){
                popupMenu.getMenu().add("Delete");
            }
        } else {
            popupMenu.getMenu().add("Report");
        }

        popupMenu.setOnMenuItemClickListener(item1 -> {
            String title = item1.getTitle().toString();
            switch (title){
                case "Edit":
                    editFoodPost();
                    break;
                case "Delete":
                    sureFragment.show(true);
                    break;
                case "Report":
                    break;
            }
            return false;
        });
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
                popupMenu.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void editFoodPost(){
        Intent k = new Intent(this, EditFoodPostActivity.class);
        k.putExtra("foodPostId", foodPostDetail.id);
        startActivityForResult(k, REQUEST_EDITED_FOODPOST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDITED_FOODPOST && resultCode == Activity.RESULT_OK) {
            foodPostDetail = (FoodPostDetail) data.getSerializableExtra("foodPost");
            setDetails();
        }
    }

    void getFoodPostDetailsAndSet(int foodPostId){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/food_with_user_status/" + foodPostId + "/").execute());
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
                return ServerAPI.get(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                foodPostDetail = new FoodPostDetail(jo.get("food_post").getAsJsonObject());
                if (jo.get("user_status_in_this_post") != null)
                    userStatusInPost = jo.get("user_status_in_this_post").getAsString();
                setDetails();
            }
            startWaitingFrame(false);
            super.onPostExecute(response);
        }

    }

    void getMyChosenCard(){
        GetMyChosenCardAsyncTask process = new GetMyChosenCardAsyncTask(getResources().getString(R.string.server) + "/my_chosen_card/");
        tasks.add(process.execute());
    }
    private class GetMyChosenCardAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetMyChosenCardAsyncTask(String uri){
            this.uri = uri;
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
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    if (jo.get("data").getAsJsonArray().size() > 0){
                        PaymentMethodObject pm = new PaymentMethodObject(jo.get("data").getAsJsonArray().get(0).getAsJsonObject());
                        cardIcon.setImageDrawable(ContextCompat.getDrawable(getApplication(), pm.brandImage));
                        cardLastNumbers.setText(pm.last4.substring(pm.last4.length() - 4));

                        pendingPaymentMethod.setVisibility(View.GONE);
                        attendMealButton.setAlpha(1);
                        attendMealButton.setClickable(true);
                        if (foodPostDetail.status.equals("OPEN") && USER.id != foodPostDetail.owner.id){
                            paymentMethod.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (USER.id != foodPostDetail.owner.id){
                            pendingPaymentMethod.setVisibility(View.VISIBLE);
                            attendMealButton.setAlpha(0.5f);
                            attendMealButton.setClickable(false);
                        }
                    }
                } else {
                    if (USER.id != foodPostDetail.owner.id){
                        pendingPaymentMethod.setVisibility(View.VISIBLE);
                        attendMealButton.setAlpha(0.5f);
                        attendMealButton.setClickable(false);
                    }
                }
            }
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

    void goToProfileView(User user){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("userId", user.id);
        startActivity(k);
    }

    void showProgress(boolean show){
        if (show){
            attendMealButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        } else {
            attendMealButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    }

    void deletePost(){
        tasks.add(new DeletePostAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostDetail.id + "/").execute());
    }

    class DeletePostAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public DeletePostAsyncTask(String uri){
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

    void createOrder(){
        PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/create_order_and_notification/");
        tasks.add(createOrder.execute(
                new String[]{"food_post_id", "" + foodPostDetail.id},
                new String[]{"additional_guests", "" + additionalGuests}
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
            showProgress(true);
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
            if (response != null){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    try{
                        OrderObject orderObject = new OrderObject(jo.get("order").getAsJsonObject());
                        goToOrder(orderObject);
                        finish();
                    } catch (Exception e){}
                } else {
                    Toast.makeText(getApplication(), jo.get("error_message").getAsString(), Toast.LENGTH_SHORT).show();
                }
            }
            showProgress(false);
            super.onPostExecute(response);
        }
    }

    void goToOrder(OrderObject orderObject){
        try{
            Intent goToOrders = new Intent(this, OrderLookActivity.class);
            goToOrders.putExtra("orderId", orderObject.id);
            startActivity(goToOrders);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onConfirmAttend(int additionalGuests) {
        this.additionalGuests = additionalGuests;
        createOrder();
    }

    @Override
    public void leftButtonPressed() {}

    @Override
    public void rightButtonPressed() {
        deletePost();
    }

    @Override
    public void onSomething() {

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
