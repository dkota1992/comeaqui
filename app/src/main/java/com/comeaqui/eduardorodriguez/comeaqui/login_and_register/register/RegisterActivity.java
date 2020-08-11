package com.comeaqui.eduardorodriguez.comeaqui.login_and_register.register;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.review.ReviewGuestActivity;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    // String name, surname, phoneNumber, password, email;

    ImageView backArrow;
    EditText username;
    EditText name;
    EditText surname;
    EditText email;
    CountryCodePicker ccp;
    EditText password;
    TextView validationText;
    Button registerButton;
    View progress;

    TextView emailValtext;
    TextView nameValtext;
    TextView surnameValtext;
    TextView usernameValtext;
    TextView passwordValtext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        backArrow = findViewById(R.id.back_arr);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name_name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.email);
        ccp = findViewById(R.id.ccp);
        password = findViewById(R.id.password);
        validationText = findViewById(R.id.validation_text);
        registerButton = findViewById(R.id.register_button);
        progress = findViewById(R.id.register_progress);

        emailValtext = findViewById(R.id.email_valtext);
        nameValtext = findViewById(R.id.name_valtext);
        surnameValtext = findViewById(R.id.surname_valtext);
        passwordValtext = findViewById(R.id.password_valtext);
        usernameValtext = findViewById(R.id.username_valtext);

        setEditText(username, usernameValtext);
        setEditText(name, nameValtext);
        setEditText(surname, surnameValtext);
        setEditText(email, emailValtext);
        setEditText(password, passwordValtext);

        registerButton.setOnClickListener((v) -> {
            register();
        });

        backArrow.setOnClickListener(v -> finish());
    }

    void register(){
        if (valid()){
            submit();
        }
    }

    void showProgress(boolean show){
        if (show){
            progress.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            registerButton.setVisibility(View.VISIBLE);
        }
    }


    void goToVerifyEmailActivity(User newUser){
        Intent imageLook = new Intent(this, VerifyEmailActivity.class);
        imageLook.putExtra("user", newUser);
        startActivity(imageLook);
    }

    void showValtext(TextView tv, String text, EditText et){
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
        et.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
    }

    boolean valid(){
        boolean valid = true;

        if (TextUtils.isEmpty(username.getText().toString()) || !username.getText().toString().matches("[A-Za-z0-9_]+")){
            showValtext(usernameValtext, "Please, insert a valid username ", username);
            valid = false;
        }

        if (TextUtils.isEmpty(name.getText().toString())){
            showValtext(nameValtext, "Please, insert a name", name);
            valid = false;
        }

        if (TextUtils.isEmpty(surname.getText().toString())){
            showValtext(surnameValtext, "Please, insert a surname", surname);
            valid = false;
        }
        String target = email.getText().toString();
        if (!(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())){
            showValtext(emailValtext, "Not a valid email", email);
            valid = false;
        }

        Pattern p = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$");
        Matcher m = p.matcher(password.getText().toString());
        if (!m.find()){
            String text =
                    "It must have at least 8 characters \n" +
                    "A digit must occur at least once \n" +
                    "A lower case letter must occur at least once \n" +
                    "An upper case letter must occur at least once \n" +
                    "A special character (!?@#$%^&+=) must occur at least once \n" +
                    "No whitespace allowed in the entire string \n";
            showValtext(passwordValtext, text, password);
            password.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
            valid = false;
        }
        if (!valid){
            validationText.setVisibility(View.VISIBLE);
        } else {
            validationText.setVisibility(View.GONE);
        }
        return valid;
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
        tasks.add(new PostAsyncTask(getResources().getString(R.string.server) + "/register/").execute(
                new String[]{"username", username.getText().toString().toLowerCase()},
                new String[]{"first_name", name.getText().toString()},
                new String[]{"last_name", surname.getText().toString()},
                new String[]{"email", email.getText().toString().toLowerCase()},
                new String[]{"password", password.getText().toString()}
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
            super.onPreExecute();
            showProgress(true);
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.uploadNoCredentials("POST", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if(response != null) {
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                try{
                    User newUser = new User(jo);
                    goToVerifyEmailActivity(newUser);
                } catch (Exception e){
                    if (jo != null && jo.get("username") != null && jo.get("username").isJsonArray()){
                        showValtext(usernameValtext, jo.get("username").getAsJsonArray().get(0).getAsString(), username);
                        validationText.setVisibility(View.VISIBLE);
                    }
                    if (jo != null && jo.get("email") != null && jo.get("email").isJsonArray()){
                        showValtext(emailValtext, jo.get("email").getAsJsonArray().get(0).getAsString(), email);
                        validationText.setVisibility(View.VISIBLE);
                    }
                }
            }
            showProgress(false);
            super.onPostExecute(response);
        }
    }

    ArrayList<AsyncTask> tasks = new ArrayList<>();
    @Override
    public void onDestroy() {
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        super.onDestroy();
    }
}
