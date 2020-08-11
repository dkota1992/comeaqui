package com.comeaqui.eduardorodriguez.comeaqui.login_and_register.phone_code_verification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class VerifyPhoneActivity extends AppCompatActivity {

    TextView emailValtext;
    EditText emailAdress;
    EditText verificationCode;
    TextView verificationValtext;
    TextView codeDidNotArrive;
    Button sendCodeButton;
    View progress;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        emailValtext = findViewById(R.id.email_valtext);
        emailAdress = findViewById(R.id.email_address);
        verificationCode = findViewById(R.id.code);
        verificationValtext = findViewById(R.id.code_vtext);
        sendCodeButton = findViewById(R.id.send_code_button);
        codeDidNotArrive = findViewById(R.id.send_again);
        progress = findViewById(R.id.send_code_progress);

        setEditText(emailAdress, emailValtext);
        setEditText(verificationCode, verificationValtext);
        sendCodeButton.setOnClickListener((v) -> sendEmailVerificationCode());
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
        tasks.add(new SendCodeAsyncTask(getResources().getString(R.string.server) + "/send_code_to_email/" + emailAdress.getText() + "/").execute());
    }
    private class SendCodeAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public SendCodeAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            sendCodeButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
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
            sendCodeButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }
    }

    void sendCode(){
        tasks.add(new CheckCodeAsyncTask(getResources().getString(R.string.server) + "/is_code_valid/" + verificationCode.getText() + "/").execute());
    }
    private class CheckCodeAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public CheckCodeAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
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
                if (new JsonParser().parse(response).getAsJsonObject().get("is_valid").getAsBoolean()){

                } else {
                    showValtext(verificationValtext, "Wrong verification code", verificationCode);
                }
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
