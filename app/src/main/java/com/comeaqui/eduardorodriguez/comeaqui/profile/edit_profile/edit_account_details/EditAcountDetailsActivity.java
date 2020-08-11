package com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.ChangePasswordActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.PaymentMethodObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.confirm_email.ConfirmEmailActivity;
import com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment.PaymentMethodsActivity;


import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class EditAcountDetailsActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private TextView phoneNumber;
    private TextView emailAddress;
    private Button changePassword;
    private TextView save;
    private ImageView paymentImage;
    private TextView paymentNumber;
    private ProgressBar progressBar;
    private LinearLayout paymentMethod;

    PaymentMethodObject pm;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_acount_details);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);
        emailAddress = findViewById(R.id.email_address);
        changePassword = findViewById(R.id.change_password);
        save = findViewById(R.id.save);
        paymentImage = findViewById(R.id.payment_image);
        paymentNumber = findViewById(R.id.credit_card_number);
        progressBar = findViewById(R.id.progressBar);
        paymentMethod = findViewById(R.id.payment_method);


    }
    @Override
    protected void onResume() {
        super.onResume();
        initializeUser();
        getMyChosenCard();
    }

    void setData(){
        firstName.setText(USER.first_name);
        lastName.setText(USER.last_name);
        phoneNumber.setText(USER.phone_number);
        emailAddress.setText(USER.email);

        paymentMethod.setOnClickListener(v -> {
            Intent paymentMethodA = new Intent(this, PaymentMethodsActivity.class);
            paymentMethodA.putExtra("changeMode", false);
            startActivity(paymentMethodA);
        });

        emailAddress.setOnClickListener(v -> goToChangeEmail());
        save.setOnClickListener(v -> saveData());
        changePassword.setOnClickListener((v) -> startActivity(new Intent(this, ChangePasswordActivity.class)));
        findViewById(R.id.back_arrow).setOnClickListener(v -> finish());
    }

    void setPaymentData(){
        paymentNumber.setText("**** " + pm.last4.substring(pm.last4.length() - 4));
        paymentImage.setImageDrawable(ContextCompat.getDrawable(this, pm.brandImage));
    }

    public void initializeUser(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_profile/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
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
                USER = new User(new JsonParser().parse(response).getAsJsonObject());
                setData();
            }
        }
    }

    void goToChangeEmail(){
        Intent confirmEmail = new Intent(this, ConfirmEmailActivity.class);
        confirmEmail.putExtra("email", emailAddress.getText().toString());
        startActivity(confirmEmail);
    }

    void showProgress(boolean show){
        progressBar.setVisibility(show ? View.VISIBLE: View.GONE);
        save.setVisibility(show ? View.GONE: View.VISIBLE);
    }

    private void saveData(){
        PatchAsyncTask putTast = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
        tasks.add(putTast.execute(
                new String[]{"first_name", firstName.getText().toString()},
                new String[]{"last_name", lastName.getText().toString()},
                new String[]{"phone_number", phoneNumber.getText().toString()}
        ));
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
            showProgress(false);
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

    void getMyChosenCard(){
        GetMyChosenCardAsyncTask process = new GetMyChosenCardAsyncTask(getResources().getString(R.string.server) + "/my_chosen_card/");
        tasks.add(process.execute());
    }
    private class GetMyChosenCardAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetMyChosenCardAsyncTask(String uri){
            this.uri = uri;
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
                if (jo.get("error_message") == null){
                    if (jo.get("data").getAsJsonArray().size() > 0){
                        pm = new PaymentMethodObject(jo.get("data").getAsJsonArray().get(0).getAsJsonObject());
                        setPaymentData();
                    }
                }
            }
            super.onPostExecute(response);
        }
    }
}
