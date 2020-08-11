package com.comeaqui.eduardorodriguez.comeaqui.general.dinner_list;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.comeaqui.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.ProfileViewActivity;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import java.io.IOException;

public class DinnerListActivity extends AppCompatActivity implements DinnerFragment.OnListFragmentInteractionListener {

    DinnerFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinners_list);

        Bundle b =  getIntent().getExtras();
        if(b != null && b.get("foodPostId") != null){
            int fpId = b.getInt("foodPostId");

            fragment = DinnerFragment.newInstance(fpId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dinners_list_frame, fragment)
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(OrderObject item) {
        goToProfileView(item.owner);
    }

    @Override
    public void onChatInteraction(OrderObject item) {
        goToConversationWithUser(item.owner);
    }

    void goToProfileView(User user){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("userId", user.id);
        startActivity(k);
    }

    void goToConversationWithUser(User user){
        fragment.startWaitingFrame(true);
        new GetAsyncTask(getResources().getString(R.string.server) + "/get_or_create_chat/" + user.id + "/").execute();
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            fragment.startWaitingFrame(true);

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
                ChatObject chat = new ChatObject(new JsonParser().parse(response).getAsJsonObject());
                goToConversationActivity(chat);
                fragment.startWaitingFrame(false);
            }
            fragment.startWaitingFrame(false);
            super.onPostExecute(response);
        }

    }

    void goToConversationActivity(ChatObject chat){
        Intent k = new Intent(this, ConversationActivity.class);
        k.putExtra("chatId", chat.id + "");
        startActivity(k);
    }
}
