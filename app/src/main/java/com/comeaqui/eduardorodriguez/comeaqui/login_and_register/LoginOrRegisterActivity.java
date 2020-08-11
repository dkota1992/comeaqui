package com.comeaqui.eduardorodriguez.comeaqui.login_and_register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.comeaqui.eduardorodriguez.comeaqui.PrepareActivity;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.register.RegisterActivity;

public class LoginOrRegisterActivity extends AppCompatActivity {

    Button registerButton;
    Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);

        SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            Intent a = new Intent(this, PrepareActivity.class);
            startActivity(a);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up);
        }

        registerButton.setOnClickListener((v) -> {
            Intent a = new Intent(this, RegisterActivity.class);
            startActivity(a);
        });

        loginButton.setOnClickListener((v) -> {
            Intent a = new Intent(this, LoginActivity.class);
            startActivity(a);
        });

    }
}
