package com.comeaqui.eduardorodriguez.comeaqui.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import static com.comeaqui.eduardorodriguez.comeaqui.App.MAX_CONNECTIONS_TRIES;
import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;
import static com.yalantis.ucrop.UCropFragment.TAG;

public class ChatActivity extends AppCompatActivity{

    ArrayList<ChatObject> data = new ArrayList<>();
    MyChatRecyclerViewAdapter adapter;
    HashMap<Integer, ChatObject> chatObjectHashMap = new HashMap<>();
    WebSocketClient mWebSocketClient;

    RecyclerView recyclerView;
    LinearLayout noMessages;
    EditText searchBox;
    ImageButton deleteText;
    ImageView noListImage;
    TextView noListMessage;
    ProgressBar loadingProgress;

    String query = "query=";
    ArrayList<AsyncTask> tasks = new ArrayList<>();


    @Override
    public void onResume() {
        super.onResume();
        getChatsAndSet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_chat);
        noMessages = findViewById(R.id.no_messages);
        searchBox = findViewById(R.id.search_box);
        deleteText = findViewById(R.id.delete_text);
        noListImage = findViewById(R.id.no_list_image);
        noListMessage = findViewById(R.id.no_list_message);
        loadingProgress = findViewById(R.id.loading_progress);

        setSearchListener();
        start();

        adapter = new MyChatRecyclerViewAdapter(data);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isRecyclerScrollable(RecyclerView recyclerView) {
                return recyclerView.computeHorizontalScrollRange() > recyclerView.getWidth() || recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isRecyclerScrollable(recyclerView)){
                    if (!recyclerView.canScrollVertically(1)) {
                        loadMoreData();
                    }
                }
            }
        });

        deleteText.setOnClickListener(v -> searchBox.setText(""));
        View backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> finish());
    }

    static long last_text_edit = 0;
    void setSearchListener(){
        final long delay = 1000;
        final Handler handler = new Handler();
        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                getChatsAndSet();
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
                    noListMessage.setText("No messages found");
                } else {
                    noListImage.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.no_messages));
                    noListMessage.setText("No messages");
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
                ChatObject chat = new ChatObject(jo);
                data.add(chat);
                chatObjectHashMap.put(chat.id, chat);
            }
            if (data.size() > 0){
                noMessages.setVisibility(View.GONE);
            } else{
                noMessages.setVisibility(View.VISIBLE);
            }
            adapter.addData(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    int page = 1;
    void getChatsAndSet(){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        page = 1;
        data = new ArrayList<>();
        chatObjectHashMap = new HashMap<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_chats/" + query.toString() + "/" + page + "/").execute());
    }

    void loadMoreData(){
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_chats/" + query.toString() + "/" + page + "/").execute());
    }

    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            loadingProgress.setVisibility(View.VISIBLE);
            super.onPreExecute();
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
            loadingProgress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }

    }

    int tries;
    Handler handler = new Handler();
    ArrayList<Toast> toasts = new ArrayList<>();
    private void start(){
        try {
            if (null != handler){
                handler.removeCallbacksAndMessages(null);
            }
            if (null != mWebSocketClient){
                mWebSocketClient.close();
            }

            tries++;
            Toast t = Toast.makeText(getApplication(), "Connecting...", Toast.LENGTH_SHORT);
            t.show();
            toasts.add(t);
            URI uri = new URI(getResources().getString(R.string.async_server) + "/ws/unread_messages/" + USER.id +  "/");
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    runOnUiThread(() -> {
                        Log.w(TAG, "Connected");
                        for (Toast t : toasts) {
                            t.cancel();
                        }
                        tries = 0;
                    });
                }
                @Override
                public void onMessage(String s) {
                   runOnUiThread(() -> {
                        JsonObject jo = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject();
                        ChatObject chatObject = new ChatObject(jo.get("chat").getAsJsonObject());
                        if (chatObjectHashMap.containsKey(chatObject.id)){
                            int index = data.indexOf(chatObjectHashMap.get(chatObject.id));
                            data.remove(index);
                            data.add(0, chatObject);
                            chatObjectHashMap.put(chatObject.id, chatObject);
                        } else {
                            data.add(0, chatObject);
                            chatObjectHashMap.put(chatObject.id , chatObject);
                        }
                        noMessages.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    });
                }
                @Override
                public void onClose(int i, String s, boolean b) {
                    runOnUiThread(() -> {
                        if (null != handler && tries < MAX_CONNECTIONS_TRIES){
                            handler.postDelayed(() -> start(), 1000);
                        }
                    });
                }
                @Override
                public void onError(Exception e) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
            };
            try {
                SSLContext sslContext = SSLContext.getDefault();
                mWebSocketClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
