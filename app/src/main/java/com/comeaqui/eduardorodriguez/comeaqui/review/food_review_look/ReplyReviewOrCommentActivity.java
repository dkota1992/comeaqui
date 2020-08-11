package com.comeaqui.eduardorodriguez.comeaqui.review.food_review_look;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments.FoodCommentFragment;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodCommentObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.ReviewReplyObject;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.message_fragments.OneOptionMessageFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class ReplyReviewOrCommentActivity extends AppCompatActivity {

    TextView review;
    TextView usernameTime;
    ImageButton close;
    Button post;
    ProgressBar progressBar;
    EditText reply;
    FrameLayout errorMessage;

    ReviewObject reviewObject;
    FoodCommentObject commentObject;
    ArrayList<AsyncTask> tasks = new ArrayList<>();
    int foodPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_review);

        review = findViewById(R.id.review);
        usernameTime = findViewById(R.id.username_and_time);
        close = findViewById(R.id.close);
        post = findViewById(R.id.post_reply);
        progressBar = findViewById(R.id.progress_reply);
        reply = findViewById(R.id.reply);
        errorMessage = findViewById(R.id.error_message);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("review") != null){
            reviewObject = (ReviewObject) b.get("review");
            setReview();
        } else if(b != null && b.get("comment") != null  && b.get("foodPostId") != null){
            commentObject = (FoodCommentObject) b.get("comment");
            foodPostId = b.getInt("foodPostId");
            setCommentObject();
        }
    }

    void setReview(){
        review.setText(reviewObject.review);
        usernameTime.setText(reviewObject.owner.username + " " + reviewObject.createdAt);
        post.setOnClickListener(v -> postReviewReply());
        close.setOnClickListener(v -> finish());
    }

    void setCommentObject(){
        review.setText(commentObject.message);
        usernameTime.setText(commentObject.owner.username + " " + commentObject.createdAt);
        post.setOnClickListener(v -> commentCreate());
        close.setOnClickListener(v -> finish());
    }

    void postReviewReply(){
        tasks.add(new PostAsyncTask(getResources().getString(R.string.server) + "/create_review_reply/").execute(
                new String[]{"reply", reply.getText().toString()},
                new String[]{"review_id", reviewObject.id + ""}
        ));
    }

    void commentCreate(){
        tasks.add(new PostAsyncTask(getResources().getString(R.string.server) + "/food_post_comment/" + foodPostId + "/").execute(
                new String[]{"post_id", ""},
                new String[]{"comment_id", commentObject.id + ""},
                new String[]{"message", reply.getText().toString()}
        ));
    }

    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public PostAsyncTask(String uri){
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
                FoodCommentFragment.updateRespondedCommentList(new FoodCommentObject(jo));
                finish();
            }
            startWaitingFrame(false);
            super.onPostExecute(response);
        }
    }

    void startWaitingFrame(boolean start){
        if (start) {
            post.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            post.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    void showErrorMessage(){
        errorMessage.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.error_message_frame, OneOptionMessageFragment.newInstance(
                        "Error during posting",
                        "Please make sure that you have connection to the internet"))
                .commit();
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
