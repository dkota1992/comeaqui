package com.comeaqui.eduardorodriguez.comeaqui.map.search_people;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class SearchPeopleActivity extends AppCompatActivity {

    ArrayList<User> users = new ArrayList<>();
    UserRecyclerViewAdapter adapter;

    RecyclerView recyclerView;
    LinearLayout noPeopleView;
    EditText searchBox;
    ImageButton deleteText;
    ImageView noListImage;
    TextView noListMessage;
    ProgressBar loadingFoodsProgress;

    String query = "query=";
    ArrayList<AsyncTask> tasks = new ArrayList<>();


    @Override
    public void onResume() {
        super.onResume();
        getPeopleAndSet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_people);

        recyclerView = findViewById(R.id.recycler_chat);
        noPeopleView = findViewById(R.id.no_messages);
        searchBox = findViewById(R.id.search_box);
        deleteText = findViewById(R.id.delete_text);
        noListImage = findViewById(R.id.no_list_image);
        noListMessage = findViewById(R.id.no_list_message);
        loadingFoodsProgress = findViewById(R.id.users_loading_progress);

        adapter = new UserRecyclerViewAdapter(users, this);
        recyclerView.setAdapter(adapter);

        setSearchListener();

        deleteText.setOnClickListener(v -> searchBox.setText(""));
        View backView = findViewById(R.id.back);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isRecyclerScrollable(RecyclerView recyclerView) {
                return recyclerView.computeHorizontalScrollRange() > recyclerView.getWidth() || recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isRecyclerScrollable(recyclerView)){
                    if (!recyclerView.canScrollVertically(1)) {
                        loadMorePeople();
                    }
                }
            }
        });

        backView.setOnClickListener(v -> finish());
    }

    static long last_text_edit = 0;
    void setSearchListener(){
        final long delay = 1000;
        final Handler handler = new Handler();
        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                getPeopleAndSet();
            }
        };
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count, int after){}
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before, int count){
                handler.removeCallbacks(input_finish_checker);
            }
            @Override
            public void afterTextChanged ( final Editable s){
                if (s.length() > 0) {
                    deleteText.setVisibility(View.VISIBLE);
                    noListImage.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.not_found));
                    noListMessage.setText("No people found");
                } else {
                    noListImage.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.not_found));
                    noListMessage.setText("No people");
                    deleteText.setVisibility(View.GONE);
                    hideKeyboard();
                }
                query = "query=" + s.toString();
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });
    }

    public void makeList(JsonArray jsonArray){
        try {
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                User chat = new User(jo);
                users.add(chat);
            }
            if (users.size() > 0){
                noPeopleView.setVisibility(View.GONE);
            } else{
                noPeopleView.setVisibility(View.VISIBLE);
            }
            adapter.addData(users);
            adapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    int page = 1;
    void getPeopleAndSet(){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        page = 1;
        users = new ArrayList<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/user_search/" + query + "&page=" + page +  "/").execute());
    }
    void loadMorePeople(){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/user_search/" + query + "&page=" + page + "/").execute());
    }

    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingFoodsProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getApplication(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                makeList(new JsonParser().parse(response).getAsJsonArray());
                page++;
            }
            loadingFoodsProgress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }

    }

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void onFragmentInteraction(User user){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("userId", user.id);
        startActivity(k);
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