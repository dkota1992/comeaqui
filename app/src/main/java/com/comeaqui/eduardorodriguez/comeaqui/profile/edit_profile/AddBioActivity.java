package com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;



import java.io.IOException;
import java.util.ArrayList;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;


public class AddBioActivity extends AppCompatActivity {

    ImageView image;
    EditText bioEditTextView;
    TextView name;
    TextView username;

    int MAX_NUMBER_OF_LETTER = 101;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bio);
        bioEditTextView = findViewById(R.id.bio_edit_text);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        image = findViewById(R.id.poster_image);
        TextView textCountView = findViewById(R.id.text_count);
        Button save = findViewById(R.id.save);

        Glide.with(this).load(USER.profile_photo).into(image);
        name.setText(USER.first_name + " " + USER.last_name);
        username.setText(USER.username);

        bioEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int letters = bioEditTextView.getText().toString().trim().length();
                textCountView.setText(letters + "/" + MAX_NUMBER_OF_LETTER);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.back_arrow).setOnClickListener(v -> finish());
        save.setOnClickListener(v -> saveImage());
    }

    private void saveImage(){
        PatchAsyncTask putTask = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
        tasks.add(putTask.execute(
                new String[]{"bio", bioEditTextView.getText().toString()}
        ));
    }
    private class PatchAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public PatchAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), "PATCH", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            finish();
            super.onPostExecute(response);
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
