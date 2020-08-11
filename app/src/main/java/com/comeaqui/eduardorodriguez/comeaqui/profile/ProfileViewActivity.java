package com.comeaqui.eduardorodriguez.comeaqui.profile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.comeaqui.eduardorodriguez.comeaqui.R;

public class ProfileViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_container, ProfileFragment.newInstance(getIntent().getExtras().getInt("userId")))
                .commit();
    }
}
