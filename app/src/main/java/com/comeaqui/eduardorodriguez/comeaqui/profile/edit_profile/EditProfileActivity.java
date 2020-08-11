package com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.objects.PaymentMethodObject;
import com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_bank_account.EditBankAccountActivity;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.SelectImageFromFragment;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.EditAcountDetailsActivity;
import com.comeaqui.eduardorodriguez.comeaqui.R;


import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity implements SelectImageFromFragment.OnFragmentInteractionListener{

    private TextView firstNameView;
    private TextView lastNameView;
    private TextView addBioView;
    private TextView phoneNumber;
    private TextView bioTextView;
    private ImageView profileImageView;
    private ImageView backgroundImageView;
    private ImageView paymentImage;
    private TextView paymentNumber;

    private SelectImageFromFragment selectImageFromFragment;

    PaymentMethodObject pm;

    boolean isBackGound;
    int userId;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    private void setProfile(User user){
        if(user.profile_photo != null && !user.profile_photo.contains("no-image")) Glide.with(this).load(user.profile_photo).into(profileImageView);
        if(user.background_photo != null && !user.background_photo.contains("no-image")) Glide.with(this).load(user.background_photo).into(backgroundImageView);
        firstNameView.setText(user.first_name);
        lastNameView.setText(user.last_name);
        phoneNumber.setText(user.phone_number);
        if (user.bio != null && !user.bio.equals(""))
            bioTextView.setText(user.bio);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profileImageView = findViewById(R.id.profile_image);
        addBioView = findViewById(R.id.add_bio);
        bioTextView = findViewById(R.id.bioText);
        TextView editProfilePhotoView = findViewById(R.id.edit_profile_picture);
        TextView editCoverPhoto = findViewById(R.id.edit_cover_photo);
        ImageView backView = findViewById(R.id.back_arrow);
        backgroundImageView = findViewById(R.id.background_image);
        TextView editAccountDetailsView = findViewById(R.id.edit_account_details);
        TextView editBankAccount = findViewById(R.id.edit_bank_account);
        firstNameView = findViewById(R.id.first_name);
        lastNameView = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);
        paymentImage = findViewById(R.id.payment_image);
        paymentNumber = findViewById(R.id.credit_card_number);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            userId = b.getInt("userId");
        }

        selectImageFromFragment = SelectImageFromFragment.newInstance(false);

        editProfilePhotoView.setOnClickListener(v -> {
            isBackGound = false;
            selectImageFromFragment.showCard();

        });


        editCoverPhoto.setOnClickListener(v -> {
            isBackGound = true;
            selectImageFromFragment.showCard();
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.select_from, selectImageFromFragment)
                .commit();

        addBioView.setOnClickListener(v -> {
            Intent bioActivity = new Intent(this, AddBioActivity.class);
            startActivity(bioActivity);
        });

        bioTextView.setOnClickListener(v -> {
            Intent bioActivity = new Intent(this, AddBioActivity.class);
            startActivity(bioActivity);
        });

        editAccountDetailsView.setOnClickListener(v -> {
            Intent bioActivity = new Intent(this, EditAcountDetailsActivity.class);
            startActivity(bioActivity);
        });

        editBankAccount.setOnClickListener(v -> {
            Intent bioActivity = new Intent(this, EditBankAccountActivity.class);
            startActivity(bioActivity);
        });

        backView.setOnClickListener(v -> finish());
    }
    void setPaymentData(){
        paymentNumber.setText("**** " + pm.last4.substring(pm.last4.length() - 4));
        paymentImage.setImageDrawable(ContextCompat.getDrawable(this, pm.brandImage));
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectImageFromFragment.hideCard();
        getUser(userId);
        getMyChosenCard();
    }

    public User getUser(int userId) {
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/profile_detail/" + userId + "/").execute());
        return null;
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
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
                setProfile(new User(new JsonParser().parse(response).getAsJsonObject()));
            }
            super.onPostExecute(response);
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        saveProfileImage(uri);
    }


    private void saveProfileImage(Uri imageUri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            PatchImageAsyncTask putTask = new PatchImageAsyncTask(getResources().getString(R.string.server) + "/edit_profile/", bitmap);
            if (isBackGound){
                tasks.add(putTask.execute("background_photo"));
            } else {
                tasks.add(putTask.execute("profile_photo"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class PatchImageAsyncTask extends AsyncTask<String, Void, String> {
        String uri;
        Bitmap imageBitmap;

        public PatchImageAsyncTask(String uri, Bitmap imageBitmap){
            this.uri = uri;
            this.imageBitmap = imageBitmap;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                return ServerAPI.uploadImage(getApplicationContext(),"PATCH", this.uri, params[0], this.imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
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
