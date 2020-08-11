package com.comeaqui.eduardorodriguez.comeaqui.review.food_review_look;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.BuildConfig;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.general.FoodLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPostReview;
import com.comeaqui.eduardorodriguez.comeaqui.objects.ReviewReplyObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.ProfileViewActivity;

import com.comeaqui.eduardorodriguez.comeaqui.server.Server;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.message_fragments.OneOptionMessageFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.message_fragments.TwoOptionsMessageFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class FoodPostReviewLookActivity extends AppCompatActivity implements MyFoodReviewRecyclerViewAdapter.OnListFragmentInteractionListener,
TwoOptionsMessageFragment.OnFragmentInteractionListener{

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private RecyclerView recList;
    private ConstraintLayout noComments;

    private ArrayList<ReviewObject> reviews;
    private MyFoodReviewRecyclerViewAdapter adapter;
    FoodPostReview foodPostReview;


    public TextView foodNameView;
    public TextView postPrice;
    public TextView posterDescriptionView;
    public TextView postNameView;
    public TextView postRating;
    public View cardButtonView;

    FrameLayout waitingFrame;
    TwoOptionsMessageFragment sureFragment;

    ImageView vegetarian;
    ImageView vegan;
    ImageView cereal;
    ImageView spicy;
    ImageView fish;
    ImageView meat;
    ImageView dairy;

    Integer fpId;
    boolean isCollapsed = true;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_post_review_look);
        toolbar = findViewById(R.id.anim_toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBarLayout = findViewById(R.id.appbar);
        recList = findViewById(R.id.scrollableview);

        foodNameView = findViewById(R.id.plateName);
        postNameView = findViewById(R.id.plate_name);
        postPrice = findViewById(R.id.price);
        posterDescriptionView = findViewById(R.id.description_post_review);
        cardButtonView = findViewById(R.id.cardButton);
        postRating = findViewById(R.id.food_post_review_rating);
        waitingFrame = findViewById(R.id.waiting_frame);
        noComments = findViewById(R.id.no_comments);

        vegetarian = findViewById(R.id.vegetarian);
        vegan = findViewById(R.id.vegan);
        cereal = findViewById(R.id.cereal);
        spicy = findViewById(R.id.spicy);
        fish = findViewById(R.id.fish);
        meat = findViewById(R.id.meat);
        dairy = findViewById(R.id.dairy);

        setToolbar();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            fpId = b.getInt("foodPostId");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.waiting_frame, WaitFragment.newInstance())
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.image_list, HorizontalImageDisplayFragment.newInstance(fpId, 0, 4, 200,0, 0))
                    .commit();

            sureFragment = TwoOptionsMessageFragment.newInstance("Delete", "Are you sure you want to delete this post?", "CANCEL", "DELETE", true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sure_message, sureFragment)
                    .commit();
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_food);
        Palette.from(bitmap).generate(palette -> {
            Palette.Swatch vibrant = palette.getVibrantSwatch();
            if (vibrant != null) {
                collapsingToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
                collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        });
    }
    void setToolbar(){
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setTitle("Reviews");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorPrimary_trans));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fpId != null){
            getReviewsFrompFoodPost(fpId);
        }
    }

    void setViewFoodPost(){
        postNameView.setText(foodPostReview.plate_name);
        posterDescriptionView.setText(foodPostReview.description);
        posterDescriptionView.setVisibility(View.VISIBLE);
        postPrice.setText(foodPostReview.price_to_show);
        postRating.setText(foodPostReview.rating_to_show);
        setTypes(foodPostReview.type);
        cardButtonView.setOnClickListener(v -> goToPostLook(foodPostReview.id));

        findViewById(R.id.other).setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });
    }

    void setTypes(String types){
        ImageView[] imageViews = new ImageView[]{
                vegetarian,
                vegan,
                cereal,
                spicy,
                fish,
                meat,
                dairy
        };
        ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
        for (ImageView imageView: imageViews){
            imageView.setVisibility(View.GONE);
            imageViewArrayList.add(imageView);
        }
        int[] resources = new int[]{
                R.drawable.vegetarianfill,
                R.drawable.veganfill,
                R.drawable.cerealfill,
                R.drawable.spicyfill,
                R.drawable.fishfill,
                R.drawable.meatfill,
                R.drawable.dairyfill,
        };
        for (int i = 0; i < types.length(); i++){
            if (types.charAt(i) == '1'){
                imageViewArrayList.get(i).setImageResource(resources[i]);
                imageViewArrayList.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    void goToPostLook(int foodPostId) {
        Intent foodLook = new Intent(this, FoodLookActivity.class);
        foodLook.putExtra("foodPostId", foodPostId);
        startActivity(foodLook);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        if (foodPostReview.owner.id == USER.id){
            popupMenu.getMenu().add("Delete");
        } else {
            popupMenu.getMenu().add("Report");
        }

        popupMenu.setOnMenuItemClickListener(item1 -> {
            String title = item1.getTitle().toString();
            switch (title){
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


    void getReviewsFrompFoodPost(int foodPostId){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/food_reviews/" + foodPostId + "/", this).execute());
    }

    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        private Context context;
        public GetAsyncTask(String uri, Context context){
            this.uri = uri;
            this.context = context;
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
                foodPostReview = new FoodPostReview(new JsonParser().parse(response).getAsJsonObject());
                reviews = foodPostReview.reviews;
                if (reviews.size() > 0){
                    noComments.setVisibility(View.GONE);
                } else {
                    noComments.setVisibility(View.VISIBLE);
                }
                adapter = new MyFoodReviewRecyclerViewAdapter(reviews, context, foodPostReview.owner);
                recList.setAdapter(adapter);
                setViewFoodPost();
                startWaitingFrame(false);
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

    void deletePost(){
        tasks.add(new DeletePostAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostReview.id + "/").execute());
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


    void showErrorMessage(String message){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.error_message_frame, OneOptionMessageFragment.newInstance(
                        "Error during posting",
                        "Please make sure that you have connection to the internet"))
                .commit();
    }
    void deleteReview(int reviewId){
        Server deleteFoodPost = new Server(this,"DELETE", getResources().getString(R.string.server) + "/delete_review/" + reviewId + "/");
        try {
            deleteFoodPost.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            showErrorMessage("Error deleting review");
        }
    }

    void deleteReply(int replyId){
        Server deleteFoodPost = new Server(this,"DELETE", getResources().getString(R.string.server) + "/delete_reply/" + replyId + "/");
        try {
            deleteFoodPost.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            showErrorMessage("Error deleting reply");
        }
    }


    @Override
    public void onReviewDelete(ReviewObject review) {
        deleteReview(review.id);
    }

    @Override
    public void onReplyDelete(ReviewReplyObject reply) {
        deleteReply(reply.id);
    }

    @Override
    public void leftButtonPressed() {}

    @Override
    public void rightButtonPressed() {
        deletePost();
    }

    @Override
    public void onReplyCreate(ReviewObject review) {
        Intent paymentMethod = new Intent(this, ReplyReviewOrCommentActivity.class);
        paymentMethod.putExtra("review", review);
        startActivity(paymentMethod);
    }
    @Override
    public void onGoToProfile(User user){
        Intent profile = new Intent(this, ProfileViewActivity.class);
        profile.putExtra("userId", user.id);
        startActivity(profile);
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
