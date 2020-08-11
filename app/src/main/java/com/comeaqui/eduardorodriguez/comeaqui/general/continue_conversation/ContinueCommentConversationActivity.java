package com.comeaqui.eduardorodriguez.comeaqui.general.continue_conversation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments.FoodCommentFragment;
import com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments.MyFoodCommentRecyclerViewAdapter;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodCommentObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;

public class ContinueCommentConversationActivity extends AppCompatActivity implements FoodCommentFragment.OnFragmentInteractionListener{

    FoodCommentFragment foodCommentFragment;
    int commentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_comment_conversation);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("commentId") != null){
            commentId = b.getInt("commentId");
        }

        foodCommentFragment = FoodCommentFragment.newInstance(commentId, "comment");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.conversation_list, foodCommentFragment)
                .commit();
    }

    @Override
    public void onSomething() {

    }
}
