package com.comeaqui.eduardorodriguez.comeaqui.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.map.add_food.FoodDateTimePickerFragment;
import com.comeaqui.eduardorodriguez.comeaqui.map.add_food.FoodTypeSelectorFragment;
import com.comeaqui.eduardorodriguez.comeaqui.map.add_food.WordLimitEditTextFragment;
import com.comeaqui.eduardorodriguez.comeaqui.map.add_food.add_images.AddImagesFragment;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.SelectImageFromFragment;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class EditFoodPostActivity extends AppCompatActivity implements
        WordLimitEditTextFragment.OnFragmentInteractionListener,
        SelectImageFromFragment.OnFragmentInteractionListener,
        FoodTypeSelectorFragment.OnFragmentInteractionListener,
        AddImagesFragment.OnFragmentInteractionListener{

    ImageButton close;
    Button edit;
    EditText plateName;
    TextView time;
    TextView price;
    ProgressBar editProgress;

    FoodTypeSelectorFragment foodTypeSelectorFragment;
    FoodDateTimePickerFragment foodTimePickerFragment;
    WordLimitEditTextFragment wordLimitEditTextFragment;
    AddImagesFragment addImageFragment;
    SelectImageFromFragment selectImageFromLayout;

    FoodPostDetail foodPostDetail;

    String description;
    String types;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        selectImageFromLayout.hideCard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_post);

        close = findViewById(R.id.close);
        edit = findViewById(R.id.post_reply);
        plateName = findViewById(R.id.postPlateName);
        time = findViewById(R.id.time);
        price = findViewById(R.id.price);
        editProgress = findViewById(R.id.edit_progress);

        foodTypeSelectorFragment = FoodTypeSelectorFragment.newInstance();
        foodTimePickerFragment = FoodDateTimePickerFragment.newInstance();
        wordLimitEditTextFragment = WordLimitEditTextFragment.newInstance();
        selectImageFromLayout = SelectImageFromFragment.newInstance(false);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null) {
            int foodPostId = b.getInt("foodPostId");
            tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostId + "/").execute());
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.post_limit_text_edit, wordLimitEditTextFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.set_foot_types_frame, foodTypeSelectorFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.select_image_from, selectImageFromLayout)
                .commit();

        addImageFragment = AddImagesFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_images_frame, addImageFragment)
                .commit();

        edit.setOnClickListener(v -> postEditFood());
        close.setOnClickListener(v -> finish());
    }

    void setViewDetails(){
        plateName.setText(foodPostDetail.plate_name);
        types = foodPostDetail.type;
        wordLimitEditTextFragment.setText(foodPostDetail.description);
        foodTypeSelectorFragment.setTypes(foodPostDetail.type);
        time.setText(foodPostDetail.time_to_show);
        price.setText(foodPostDetail.price_to_show);
    }

    void showProgress(boolean show){
        if (show){
            edit.setVisibility(View.GONE);
            editProgress.setVisibility(View.VISIBLE);
        } else {
            edit.setVisibility(View.VISIBLE);
            editProgress.setVisibility(View.GONE);
        }
    }

    void postEditFood(){
        if(validateFrom()){
            edit();
        }
    }

    void edit(){
        patchPost();
        showProgress(true);
    }

    void patchPost(){
        tasks.add(new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_food/" + foodPostDetail.id + "/").execute(
                new String[]{"plate_name", plateName.getText().toString()},
                new String[]{"food_type", types},
                new String[]{"description", description}
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
            if (response != null){
                foodPostDetail = new FoodPostDetail(new JsonParser().parse(response).getAsJsonObject());
                setResult(Activity.RESULT_OK, new Intent().putExtra("foodPost", foodPostDetail));
                finish();
            }
            showProgress(false);
            super.onPostExecute(response);
        }
    }

    boolean validateFrom(){
        boolean isValid = true;
        if (plateName.getText().toString().trim().equals("")){
            plateName.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
            isValid = false;
        }
        if (description.trim().equals("")){
            wordLimitEditTextFragment.setErrorBackground(true);
            isValid = false;
        }
        if (!isValid){
            Toast.makeText(this, "Some fields are not valid", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    @Override
    public void onTextChanged(String description) {
        this.description = description;
    }

    @Override
    public void onAddImage() {
        selectImageFromLayout.showCard();
    }


    @Override
    public void onFragmentInteraction(boolean[] pressed) {
        types = setTypes(pressed);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        selectImageFromLayout.hideCard();
        addImageFragment.addImage(uri);
    }

    private String setTypes(boolean[] pressed){
        StringBuilder types = new StringBuilder();
        for (boolean p: pressed){
            if (p) {
                types.append(1);
            }else{
                types.append(0);
            }
        }
        return types.toString();
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
                foodPostDetail = new FoodPostDetail(new JsonParser().parse(response).getAsJsonObject());
                addImageFragment.initializeFoodPost(foodPostDetail.id);
                setViewDetails();
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
}
