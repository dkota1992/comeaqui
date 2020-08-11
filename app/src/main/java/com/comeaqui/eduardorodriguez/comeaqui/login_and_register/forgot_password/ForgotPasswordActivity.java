package com.comeaqui.eduardorodriguez.comeaqui.login_and_register.forgot_password;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
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
import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.LoginActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI.readStream;

public class ForgotPasswordActivity extends AppCompatActivity {

    LinearLayout sendEmailFrom;
    TextView emailValtext;
    EditText emailAdress;
    TextView resendPassword;
    Button sendPassword;
    View progress;
    Button goToLogin;

    boolean passwordChanged = false;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        sendEmailFrom = findViewById(R.id.send_email_form);
        emailValtext = findViewById(R.id.email_vtext);
        emailAdress = findViewById(R.id.email_address);
        sendPassword = findViewById(R.id.send_code_button);
        resendPassword = findViewById(R.id.send_again);
        goToLogin = findViewById(R.id.go_to_login);
        progress = findViewById(R.id.forgot_password_progress);

        setEditText(emailAdress, emailValtext);
        sendPassword.setOnClickListener((v) -> sendEmail());
        resendPassword.setOnClickListener((v) -> sendEmail());
        goToLogin.setOnClickListener((v) -> {
            Intent a = new Intent(this, LoginActivity.class);
            startActivity(a);
        });
    }

    void sendEmail(){
        if (emailValid()){
            submitEmail();
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
            public void afterTextChanged(Editable s) {}
        });
    }

    void goToLogin(){
        Intent a = new Intent(this, LoginActivity.class);
        startActivity(a);
    }

    void showProgress(boolean show){
        sendPassword.setVisibility(show ? View.GONE : View.VISIBLE);
        resendPassword.setVisibility(show ? View.GONE : View.VISIBLE);
        progress.setVisibility(show? View.VISIBLE : View.GONE);
    }

    void submitEmail(){
        tasks.add(new SendNewPassword(getResources().getString(R.string.server) + "/send_new_password/" + emailAdress.getText() + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
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
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                try {
                    User newUser = new User(jo);
                    sendEmailFrom.setVisibility(View.GONE);
                    goToLogin.setVisibility(View.VISIBLE);
                } catch (Exception e){
                    if (jo.get("error_message") != null){
                        showValtext(emailValtext, jo.get("error_message").getAsString(), emailAdress);
                    }
                }
            }
            showProgress(false);
            super.onPostExecute(response);
        }
    }

    public class  SendNewPassword extends AsyncTask<String[], Void, String> {
        private String uri;

        public SendNewPassword(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params)
        {
            InputStream stream = null;
            HttpURLConnection connection = null;
            String result = null;
            try {
                connection = (HttpURLConnection) new URL(this.uri).openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) {
                    return readStream(connection.getErrorStream());
                }
                stream = connection.getInputStream();
                if (stream != null) {
                    result = readStream(stream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                try {
                    User newUser = new User(jo);
                    sendEmailFrom.setVisibility(View.GONE);
                    goToLogin.setVisibility(View.VISIBLE);
                    passwordChanged = true;
                } catch (Exception e){
                    if (jo.get("error_message") != null){
                        showValtext(emailValtext, jo.get("error_message").getAsString(), emailAdress);
                    }
                }
            }
            showProgress(false);
            resendPassword.setVisibility(View.VISIBLE);
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

    @Override
    public void finish() {
        if (passwordChanged){
            goToLogin();
        } else {
            super.finish();
        }
    }
}