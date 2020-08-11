package com.comeaqui.eduardorodriguez.comeaqui.profile.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.LoginOrRegisterActivity;

public class SettingsActivity extends AppCompatActivity {

    TextView signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        signOut = findViewById(R.id.sign_out);
        signOut.setOnClickListener((v) -> signOut());
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
}
