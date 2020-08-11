package com.comeaqui.eduardorodriguez.comeaqui.utilities.image_view_pager;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.comeaqui.eduardorodriguez.comeaqui.R;

import java.util.ArrayList;

public class ImagePagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("image_urls") != null) {
            ArrayList<String> imageUrls = b.getStringArrayList("image_urls");
            int index = b.getInt("index", 0);

            ViewPager viewPager = findViewById(R.id.view_pager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageUrls, index);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(index);

        }

        findViewById(R.id.close).setOnClickListener((v) -> finish());
    }
}
