package com.comeaqui.eduardorodriguez.comeaqui.login_and_register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.forgot_password.ForgotPasswordActivity;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {

    LinearLayout setPasswordForm;
    TextView oldPasswordValtext;
    EditText oldPassword;
    EditText newPassword;
    TextView newPasswordValtext;
    Button setPasswordButton;
    TextView passwordSetText;
    Button goToLogin;
    View progress;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    boolean passwordChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        setPasswordForm = findViewById(R.id.set_password_form);
        oldPasswordValtext = findViewById(R.id.old_password_valtext);
        oldPassword = findViewById(R.id.old_password);
        newPasswordValtext = findViewById(R.id.new_password_valtext);
        newPassword = findViewById(R.id.new_password);
        setPasswordButton = findViewById(R.id.set_password);
        passwordSetText = findViewById(R.id.password_set_text);
        goToLogin = findViewById(R.id.go_to_login);
        progress = findViewById(R.id.set_password_progress);

        setEditText(newPassword, newPasswordValtext);
        setEditText(oldPassword, oldPasswordValtext);
        setPasswordButton.setOnClickListener((v) -> sendEmail());
        goToLogin.setOnClickListener((v) -> {
            goToLogin();
        });
        findViewById(R.id.forgot_pw).setOnClickListener((v) -> {
            Intent a = new Intent(this, ForgotPasswordActivity.class);
            startActivity(a);
        });
    }

    void sendEmail(){
        if (emailValid()){
            submit();
        }
    }

    void goToLogin(){
        Intent a = new Intent(this, LoginActivity.class);
        startActivity(a);
    }

    boolean emailValid(){
        Pattern p = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$");
        Matcher m = p.matcher(newPassword.getText().toString());
        if (!m.find()){
            String text = "A digit must occur at least once \n" +
                    "A lower case letter must occur at least once \n" +
                    "An upper case letter must occur at least once \n" +
                    "A special character (!?@#$%^&+=) must occur at least once \n" +
                    "No whitespace allowed in the entire string \n";
            showValtext(newPasswordValtext, text, newPassword);
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

    void submit(){
        tasks.add(new PatchAsyncTask(getResources().getString(R.string.server) + "/password_change/").execute(
                new String[]{"old_password", oldPassword.getText().toString()},
                new String[]{"new_password", newPassword.getText().toString()}
                ));
    }

    void showProgress(boolean show){
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        setPasswordButton.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    private class PatchAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public PatchAsyncTask(String uri){
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
                return ServerAPI.upload(getApplicationContext(), "PATCH", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("old_password") != null){
                    showValtext(oldPasswordValtext, jo.get("old_password").getAsJsonArray().get(0).getAsString(), oldPassword);
                } else {
                    setPasswordForm.setVisibility(View.GONE);
                    passwordSetText.setVisibility(View.VISIBLE);
                    goToLogin.setVisibility(View.VISIBLE);
                    passwordChanged = true;
                }
            }
            showProgress(false);
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
