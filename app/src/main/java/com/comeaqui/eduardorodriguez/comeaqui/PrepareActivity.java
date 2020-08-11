package com.comeaqui.eduardorodriguez.comeaqui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.LoginOrRegisterActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;


import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;
import static com.yalantis.ucrop.UCropFragment.TAG;

public class PrepareActivity extends AppCompatActivity {

    boolean gotTimezone = false;
    String tab;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("tab") != null) {
            tab = b.getString("tab");
        }

        initializeUser();
    }

    private void getFirebaseToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();
                System.out.println("TOKEEEEEEEEN " + token);

                postTokenToServer(token);
            });
    }

    private void postTokenToServer(String token){
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        tasks.add(new PostAsyncTask(getResources().getString(R.string.server) + "/fcm/v1/devices/").execute(
                new String[]{"dev_id", androidID},
                new String[]{"reg_id", token},
                new String[]{"name", "" + USER.id}
        ));
    }
    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
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
            goToMain();
            super.onPostExecute(response);
        }
    }

    void goToLogingOrRegister(){
        Intent a = new Intent(this, LoginOrRegisterActivity.class);
        startActivity(a);
    }

    public void initializeUser(){
        tasks.add(new GetAsyncTask( getResources().getString(R.string.server) + "/my_profile/").execute());
    }
    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
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
                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("user", response);
                editor.apply();
                try {
                    USER = new User(new JsonParser().parse(response).getAsJsonObject());
                    getFirebaseToken();
                } catch (Exception e){
                    SharedPreferences pref = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt = pref.edit();
                    edt.putBoolean("signed_in", false);
                    edt.remove("email");
                    edt.remove("password");
                    edt.apply();
                    goToLogingOrRegister();
                }
            } else {
                signOut();
            }
            super.onPostExecute(response);
        }

    }


    void goToMain(){
        Intent k = new Intent(this, MainActivity.class);
        k.putExtra("tab", tab);
        startActivity(k);
        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
    }

    private void signOut(){
        SharedPreferences pref = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("signed_in", false);
        edt.remove("email");
        edt.remove("password");
        edt.apply();

        Intent bactToLogin = new Intent(this, LoginOrRegisterActivity.class);
        startActivity(bactToLogin);
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
