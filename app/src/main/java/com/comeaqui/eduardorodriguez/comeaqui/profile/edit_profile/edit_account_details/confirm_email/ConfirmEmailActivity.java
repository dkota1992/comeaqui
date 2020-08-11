package com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.confirm_email;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;


import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class ConfirmEmailActivity extends AppCompatActivity {

    TextView emailValtext;
    EditText emailAdress;
    EditText verificationCode;
    TextView verificationValtext;
    TextView codeDidNotArrive;
    Button saveEmail;
    Button sendCodeButton;
    View progress;
    View progress2;
    TextView emailSavedMessage;
    LinearLayout wholeSendCode;

    String emailToSend;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);

        emailValtext = findViewById(R.id.email_vtext);
        emailAdress = findViewById(R.id.email_address);
        verificationCode = findViewById(R.id.code);
        verificationValtext = findViewById(R.id.code_vtext);
        saveEmail = findViewById(R.id.save_email);
        sendCodeButton = findViewById(R.id.send_code_button);
        codeDidNotArrive = findViewById(R.id.send_again);
        progress = findViewById(R.id.send_code_progress);
        progress2 = findViewById(R.id.send_code_progress2);
        wholeSendCode = findViewById(R.id.whole_send_code);
        emailSavedMessage = findViewById(R.id.email_saved_message);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null){
            String email = b.getString("email");
            emailAdress.setText(email);
        }

        setEditText(emailAdress, emailValtext);
        setEditText(verificationCode, verificationValtext);
        saveEmail.setOnClickListener((v) -> sendEmailVerificationCode());
        sendCodeButton.setOnClickListener((v) -> sendCode());
        codeDidNotArrive.setOnClickListener((v) -> sendEmailVerificationCode());
    }

    void sendEmailVerificationCode(){
        if (emailValid()){
            submit();
        }
    }

    boolean emailValid(){
        String target = emailAdress.getText().toString();
        if (!(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())){
            showValtext(emailValtext, "Not a valid email", emailAdress);
            return false;
        }
        return true;
    }

    void showValtext(TextView tv, String text, EditText et){
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
        et.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
    }

    void setEditText(EditText editText, TextView valtext){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape));
                valtext.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    void submit(){
        saveEmail.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        emailToSend = emailAdress.getText().toString();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/send_code_to_email/" + emailToSend + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            saveEmail.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
                saveEmail.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            try{
                new User(new JsonParser().parse(response).getAsJsonObject());
                progress.setVisibility(View.GONE);
                wholeSendCode.setVisibility(View.VISIBLE);
            } catch(Exception e){
                showValtext(emailValtext, new JsonParser().parse(response).getAsJsonObject().get("message").getAsString(), emailAdress);
                saveEmail.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
            super.onPostExecute(response);
        }
    }

    void sendCode(){
       tasks.add(new PostAsyncTask(getResources().getString(R.string.server) + "/is_code_valid/").execute(
                    new String[]{"code", verificationCode.getText().toString()},
                    new String[]{"new_email", emailToSend}
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
            sendCodeButton.setVisibility(View.GONE);
            progress2.setVisibility(View.VISIBLE);
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
            try{
                new User(new JsonParser().parse(response).getAsJsonObject());
                progress2.setVisibility(View.GONE);
                emailSavedMessage.setVisibility(View.VISIBLE);

            } catch(Exception e){
                showValtext(verificationValtext, new JsonParser().parse(response).getAsJsonObject().get("message").getAsString(), verificationCode);
                sendCodeButton.setVisibility(View.VISIBLE);
                progress2.setVisibility(View.GONE);
            }
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
