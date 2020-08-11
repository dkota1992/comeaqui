package com.comeaqui.eduardorodriguez.comeaqui.login_and_register.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.StackView;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.LoginActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

public class VerifyEmailActivity extends AppCompatActivity {

    LinearLayout codeStack;
    TextView codeValText;
    EditText codeEditText;
    Button sendCodeButton;
    ProgressBar sendCodeProgress;
    Button sendCodeAgainButton;

    LinearLayout goToLoginStackView;
    ProgressBar progress;



    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        codeStack = findViewById(R.id.codeStack);
        codeValText = findViewById(R.id.code_validation_text);
        codeEditText = findViewById(R.id.code_view);
        sendCodeButton = findViewById(R.id.send_code);
        sendCodeProgress = findViewById(R.id.sendCodeProgress);
        sendCodeAgainButton = findViewById(R.id.code_did_not_arrive);


        goToLoginStackView = findViewById(R.id.go_to_login_stack);
        TextView text = findViewById(R.id.text);
        Button goToLogin = findViewById(R.id.go_to_login);
        progress = findViewById(R.id.progress);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("user") != null) {
            user = (User) b.getSerializable("user");
            text.setText("Thank you " + user.first_name + " for subscribing to ComeAqui!");
        }
        setEditText(codeEditText, codeValText);

        sendCodeButton.setOnClickListener((v) -> {
            sendCode();
        });
        sendCodeAgainButton.setOnClickListener((v) -> {
            sendVerificationEmailAgain();
        });

        goToLogin.setOnClickListener((v) -> {
            goToLogin();
        });

    }

    void goToLogin(){
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    void sendCode(){
        new SendCodeGetAsyncTask(getResources().getString(R.string.server) + "/send_verification_code/" + user.email + "/" + (codeEditText.getText().toString().isEmpty() ? "0": codeEditText.getText()) + "/").execute();
    }
    private class SendCodeGetAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
        String uri;

        public SendCodeGetAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            sendCodeAgainButton.setVisibility(View.GONE);
            sendCodeButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.getNoCredentials(this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                try {
                    User newUser = new User(jo);
                    codeStack.setVisibility(View.GONE);
                    goToLoginStackView.setVisibility(View.VISIBLE);
                } catch (Exception e){
                    if (jo.get("error_message") != null){
                        codeValText.setVisibility(View.VISIBLE);
                    }
                }
            }
            sendCodeAgainButton.setVisibility(View.VISIBLE);
            sendCodeButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }
    }

    void sendVerificationEmailAgain(){
        new GetAsyncTask(getResources().getString(R.string.server) + "/send_verification_email_again/" + user.email + "/").execute();
    }
    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
        String uri;

        public GetAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            sendCodeAgainButton.setVisibility(View.GONE);
            sendCodeButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.getNoCredentials(this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            sendCodeAgainButton.setVisibility(View.VISIBLE);
            sendCodeButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }
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

}
