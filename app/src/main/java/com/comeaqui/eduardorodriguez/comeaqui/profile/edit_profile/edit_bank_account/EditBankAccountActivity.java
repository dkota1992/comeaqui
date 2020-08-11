package com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_bank_account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.StripeAccountInfoObject;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditBankAccountActivity extends AppCompatActivity {

    ImageView backArrow;
    EditText firstName;
    EditText lastName;
    EditText birth;
    EditText ssn;

    CardView replaceCardFront;
    Button replaceFront;
    CardView cardFront;
    ImageView imageFront;

    CardView replaceCardBack;
    Button replaceBack;
    CardView cardBack;
    ImageView imageBack;

    Button idFront;
    ProgressBar frontProgress;
    Button idBack;
    ProgressBar backProgress;

    EditText phone;
    EditText address1;
    EditText address2;
    EditText city;
    EditText state;
    EditText zip;
    EditText country;
    EditText routingN;
    EditText accountN;

    TextView firstNameVal;
    TextView lastNameVal;
    TextView birthVal;
    TextView ssnVal;
    LinearLayout ssnProvidedView;
    Button ssnReplaceButton;
    TextView phoneVal;
    TextView address1Val;
    TextView address2Val;
    TextView cityVal;
    TextView stateVal;
    TextView zipVal;
    TextView countryVal;
    TextView routingVal;
    TextView accountVal;

    TextView accountMessage;
    CountryCodePicker ccp;
    TextView validationText;
    Button saveButton;
    View progress;

    Integer day;
    Integer mon;
    Integer year;
    String currentS = "";

    boolean isFront;
    boolean isBack;
    boolean replaceSNN = false;
    StripeAccountInfoObject accountInfoObject;
    ArrayList<AsyncTask> tasks = new ArrayList<>();
    int color_red = Color.parseColor("#85FF0000");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bank_account);
        backArrow = findViewById(R.id.back_arrow);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        birth = findViewById(R.id.date_of_birth);
        ssn = findViewById(R.id.ssn_digits);

        idFront = findViewById(R.id.id_front);
        replaceCardFront = findViewById(R.id.replace_card_front);
        replaceFront = findViewById(R.id.id_front_replace);
        cardFront = findViewById(R.id.card_front);
        frontProgress = findViewById(R.id.front_progress);
        imageFront = findViewById(R.id.image_front);

        idBack = findViewById(R.id.id_back);
        replaceCardBack = findViewById(R.id.replace_card_back);
        replaceBack = findViewById(R.id.id_back_replace);
        cardBack = findViewById(R.id.card_back);
        backProgress = findViewById(R.id.back_progress);
        imageBack = findViewById(R.id.image_back);

        phone = findViewById(R.id.phone);
        address1 = findViewById(R.id.address_line_1);
        address2 = findViewById(R.id.address_line_2);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        zip = findViewById(R.id.zip_code);
        country = findViewById(R.id.country);
        routingN = findViewById(R.id.routing_n);
        accountN = findViewById(R.id.account_n);

        firstNameVal = findViewById(R.id.first_name_val_text);
        lastNameVal = findViewById(R.id.last_name_val_text);
        birthVal = findViewById(R.id.date_of_birth_val_text);
        ssnVal = findViewById(R.id.ssn_val_text);
        phoneVal = findViewById(R.id.phone_val_text);
        address1Val = findViewById(R.id.address1_val_text);
        address2Val = findViewById(R.id.address2_val_text);
        cityVal = findViewById(R.id.city_val_text);
        stateVal = findViewById(R.id.state_val_text);
        zipVal = findViewById(R.id.zip_val_text);
        countryVal = findViewById(R.id.country_val_text);
        routingVal = findViewById(R.id.routing_n_val_text);
        accountVal = findViewById(R.id.account_n_val_text);

        ccp = findViewById(R.id.ccp);
        validationText = findViewById(R.id.validation_text);
        saveButton = findViewById(R.id.save_button);
        progress = findViewById(R.id.register_progress);
        accountMessage = findViewById(R.id.account_message);

        ssnProvidedView = findViewById(R.id.ssn_provided);
        ssnReplaceButton = findViewById(R.id.replace_ssn);

        setEditText(firstName, firstNameVal);
        setEditText(lastName, lastNameVal);
        setEditText(birth, birthVal);
        setEditText(ssn, ssnVal);
        setEditText(phone, phoneVal);
        setEditText(address1, address1Val);
        setEditText(address2, address2Val);
        setEditText(city, cityVal);
        setEditText(state, stateVal);
        setEditText(zip, zipVal);
        setEditText(country, countryVal);
        setEditText(routingN, routingVal);
        setEditText(accountN, accountVal);

        saveButton.setOnClickListener((v) -> {
            if (true || validateFrom()){
                submit("PATCH");
            }
        });

        getBankAccountInfo();

        backArrow.setOnClickListener(v -> finish());

        birth.setOnClickListener(v -> birth.setSelection(birth.getText().length()));
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String st = s.toString();
                if (!currentS.equals(st)){
                    String st_r = st.replaceAll("[^0-9]", "");
                    StringBuilder s_to_show = new StringBuilder();
                    for (int i = 0; i < st_r.length(); i++){
                        if (i == 2 || i == 4){
                            s_to_show.append("/");
                        }
                        s_to_show.append(st_r.charAt(i));
                    }
                    if (st_r.length() == 8){
                        mon  = Integer.parseInt(st_r.substring(0,2));
                        day  = Integer.parseInt(st_r.substring(2,4));
                        year = Integer.parseInt(st_r.substring(4,8));
                    } else {
                        day  = null;
                        mon  = null;
                        year = null;
                    }
                    currentS = s_to_show.toString();
                    birth.setText(s_to_show.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println(day + "," + mon + "," + year);
                birth.setSelection(birth.getText().length());
                birth.setHintTextColor(color_red);
            }
        };
        birth.addTextChangedListener(tw);
    }
    boolean isDateValid() {
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            df.setLenient(false);
            df.parse(birth.getText().toString());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    void setInfo(){
        if (accountInfoObject.individual.first_name != null && !accountInfoObject.individual.first_name.isEmpty()) {
            firstName.setHint(accountInfoObject.individual.first_name);
        }
        if (accountInfoObject.individual.last_name != null && !accountInfoObject.individual.last_name.isEmpty()) {
            lastName.setHint(accountInfoObject.individual.last_name);
        }
        if (
            accountInfoObject.individual.dob.year != null &&
            accountInfoObject.individual.dob.month != null &&
            accountInfoObject.individual.dob.day != null
        ){
            String m = accountInfoObject.individual.dob.month < 10 ? "0" + accountInfoObject.individual.dob.month: accountInfoObject.individual.dob.month + "";
            String d = accountInfoObject.individual.dob.day < 10 ? "0" + accountInfoObject.individual.dob.day: accountInfoObject.individual.dob.day + "";
            birth.setHint(m + "/" + d + "/" + accountInfoObject.individual.dob.year);
        }
        if (accountInfoObject.individual.ssn_last_4_provided){
            ssnProvidedView.setVisibility(View.VISIBLE);
            ssnReplaceButton.setOnClickListener(v -> {
                replaceSNN = true;
                ssn.setVisibility(View.VISIBLE);
                ssnProvidedView.setVisibility(View.INVISIBLE);
            });
            ssn.setVisibility(View.INVISIBLE);
        }
        if (accountInfoObject.individual.verification.document.front != null) {
            replaceCardFront.setVisibility(View.VISIBLE);
            cardFront.setVisibility(View.GONE);
        }
        if (accountInfoObject.individual.verification.document.back != null) {
            replaceCardBack.setVisibility(View.VISIBLE);
            cardBack.setVisibility(View.GONE);
        }
        if (accountInfoObject.individual.id_number != null) {
            ssn.setHint(accountInfoObject.individual.id_number);
        }
        if (accountInfoObject.individual.phone != null && !accountInfoObject.individual.phone.isEmpty()) {
            phone.setHint(accountInfoObject.individual.phone);
        }
        if (accountInfoObject.individual.address.line1 != null && !accountInfoObject.individual.address.line1.isEmpty()) {
            address1.setHint(accountInfoObject.individual.address.line1);
        }
        if (accountInfoObject.individual.address.line2 != null && !accountInfoObject.individual.address.line2.isEmpty()) {
            address2.setHint(accountInfoObject.individual.address.line2);
        }
        if (accountInfoObject.individual.address.city != null && !accountInfoObject.individual.address.city.isEmpty()) {
            city.setHint(accountInfoObject.individual.address.city);
        }
        if (accountInfoObject.individual.address.state != null && !accountInfoObject.individual.address.state.isEmpty()) {
            state.setHint(accountInfoObject.individual.address.state);
        }
        if (accountInfoObject.individual.address.postal_code != null && !accountInfoObject.individual.address.postal_code.isEmpty()) {
            zip.setHint(accountInfoObject.individual.address.postal_code);
        }
        if (accountInfoObject.individual.address.country != null && !accountInfoObject.individual.address.country.isEmpty()) {
            country.setHint(accountInfoObject.individual.address.country);
        }
        ArrayList<StripeAccountInfoObject.Account> accounts = accountInfoObject.external_accounts.data;
        if (accounts.size() > 0){
            routingN.setHint(accounts.get(0).routing_number);
            accountN.setHint("Ending " + accounts.get(0).last4);
        } else {
            routingN.setHintTextColor(color_red);
            accountN.setHintTextColor(color_red);
        }

        if (accountInfoObject.payouts_enabled){
            accountMessage.setText("Account verified");
            accountMessage.setBackground(ContextCompat.getDrawable(this, R.color.success));
        } else if (accountInfoObject.requirements.currently_due.size() == 0){
            accountMessage.setText("Pending review");
            accountMessage.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimary));
        } else {
            accountMessage.setText("Account incomplete");
            accountMessage.setBackground(ContextCompat.getDrawable(this, R.color.canceled));
        }
        accountMessage.setVisibility(View.VISIBLE);

        for (String due: accountInfoObject.requirements.currently_due){
            switch (due){
                case "individual.address.line1":
                    address1.setHintTextColor(color_red);
                    break;
                case "individual.address.line2":
                    address2.setHintTextColor(color_red);
                    break;
                case "individual.address.country":
                    country.setHintTextColor(color_red);
                    break;
                case "individual.address.city":
                    city.setHintTextColor(color_red);
                    break;
                case "individual.address.postal_code":
                    zip.setHintTextColor(color_red);
                    break;
                case "individual.address.state":
                    state.setHintTextColor(color_red);
                    break;
                case "individual.dob.day":
                    birth.setHintTextColor(color_red);
                    break;
                case "individual.dob.month":
                    birth.setHintTextColor(color_red);
                    break;
                case "individual.dob.year":
                    birth.setHintTextColor(color_red);
                    break;
                case "individual.first_name":
                    firstName.setHintTextColor(color_red);
                    break;
                case "individual.last_name":
                    lastName.setHintTextColor(color_red);
                    break;
                case "individual.phone":
                    phone.setHintTextColor(color_red);
                    break;
                case "individual.ssn_last_4":
                    ssn.setHintTextColor(color_red);
                    break;
                case "individual.id_number":
                    ssn.setHintTextColor(color_red);
                    break;
            }
        }

        idFront.setOnClickListener(v -> {
            isFront = true;
            checkPermissions();
        });
        idBack.setOnClickListener(v -> {
            isBack = true;
            checkPermissions();
        });

        replaceFront.setOnClickListener(v -> {
            replaceCardFront.setVisibility(View.GONE);
            cardFront.setVisibility(View.VISIBLE);
        });
        replaceBack.setOnClickListener(v -> {
            replaceCardBack.setVisibility(View.GONE);
            cardBack.setVisibility(View.VISIBLE);
        });
    }

    boolean validateFrom(){
        boolean isValid = true;
        if (!birth.getText().toString().isEmpty() && !isDateValid()){
            showValtext(birthVal, "Please, insert a valid date", birth);
            isValid = false;
        }
        if (!ssn.getText().toString().isEmpty() && ssn.getText().toString().length() < 9){
            showValtext(ssnVal, "SSN number should be at least 9 digits", ssn);
            isValid = false;
        }
        if (!routingN.getText().toString().isEmpty() && routingN.getText().toString().length() < 9){
            showValtext(routingVal, "Routing number should be at least 9 digits", routingN);
            isValid = false;
        }
        return isValid;
    }

    void getBankAccountInfo(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/stripe_account/").execute());
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
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    accountInfoObject = new StripeAccountInfoObject(jo);
                    setInfo();
                }
            }
            showProgress(false);
            super.onPostExecute(response);
        }
    }

    void showProgress(boolean show){
        if (show){
            progress.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        }
    }

    void submit(String method){
        tasks.add(new UploadAsyncTask(method,getResources().getString(R.string.server) + "/stripe_account/").execute(
                new String[]{"first_name", firstName.getText().toString()},
                new String[]{"last_name", lastName.getText().toString()},
                new String[]{"day", day == null ? "" : day + ""},
                new String[]{"month", mon == null ? "" : mon + ""},
                new String[]{"year", year == null ? "" : year + ""},
                new String[]{"ssn_last_4", ssn.getText().toString()},
                new String[]{"id_number", ssn.getText().toString()},
                new String[]{"phone", phone.getText().toString()},
                new String[]{"line1", address1.getText().toString()},
                new String[]{"line2", address2.getText().toString()},
                new String[]{"city", city.getText().toString()},
                new String[]{"state", state.getText().toString()},
                new String[]{"postal_code", zip.getText().toString()},
                new String[]{"country", country.getText().toString()},
                new String[]{"routing_number", routingN.getText().toString()},
                new String[]{"account_number", accountN.getText().toString()}
        ));
    }
    private class UploadAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        String method;
        public UploadAsyncTask(String method, String uri){
            this.method = method;
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            showProgress(true);
            showUploadProgress(true, "front");
            showUploadProgress(true, "back");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), method, this.uri, params);
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
                    Toast.makeText(getApplication(), "Account saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String e_message = jo.get("error_message").getAsString();

                    if (e_message.contains("individual[first_name]")) {
                        showValtext(firstNameVal, "The first name is not valid", firstName);
                    }
                    if (e_message.contains("individual[last_name]")) {
                        showValtext(lastNameVal, "The last name is not valid", lastName);
                    }
                    if (
                        e_message.contains("individual[dob][year]") ||
                        e_message.contains("individual[dob][day]") ||
                        e_message.contains("individual[dob][month]")
                    ) {
                        showValtext(birthVal,"The date of birth is not valid", birth);
                    }
                    if (e_message.contains("individual[ssn_last_4]")) {
                        showValtext(ssnVal, "The ssn number is not valid", ssn);
                    }
                    if (e_message.contains("individual[id_number]")) {
                        showValtext(ssnVal, "The SSN number is not valid", ssn);
                    }
                    if (e_message.contains("individual[phone]")) {
                        showValtext(phoneVal, "The phone number is not valid", phone);
                    }
                    if (e_message.contains("individual[address][line1]")) {
                        showValtext(address1Val, "Invalid address", address1);
                    }
                    if (e_message.contains("individual[address][line2]")) {
                        showValtext(address2Val, "The address", address2);
                    }
                    if (e_message.contains("individual[address][city]")) {
                        showValtext(cityVal, "Invalid city", city);
                    }
                    if (e_message.contains("individual[address][state]")) {
                        showValtext(stateVal, "Invalid city", state);
                    }
                    if (e_message.contains("individual[address][postal_code]")) {
                        showValtext(zipVal, "Invalid US postal code", zip);
                    }
                    if (e_message.contains("individual[address][country]")) {
                        showValtext(countryVal, "The Country is not valid", country);
                    }
                    if (e_message.contains("external_account[routing_number]")) {
                        showValtext(routingVal, "The routing number is not valid", routingN);
                    }
                    if (e_message.contains("external_account[account_number]")) {
                        showValtext(accountVal, "The account number is not valid", accountN);
                    }

                    Toast.makeText(getApplication(), "Some errors have occurred", Toast.LENGTH_SHORT).show();
                }
            }
            showProgress(false);
            showUploadProgress(false, "front");
            showUploadProgress(false, "back");
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



    public void uploadImage(Bitmap imageBitmap, String param){
        PostImagesAsyncTask postI = new PostImagesAsyncTask(
                getResources().getString(R.string.server) + "/upload_stripe_document/",
                imageBitmap,
                param
        );
        tasks.add(postI.execute());
    }

    class PostImagesAsyncTask extends AsyncTask<String, Void, String> {
        String uri;
        Bitmap image;
        String param;
        public PostImagesAsyncTask(String uri, Bitmap imageBitmaps, String param){
            this.uri = uri;
            this.image = imageBitmaps;
            this.param = param;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showUploadProgress(true, param);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return ServerAPI.uploadImage(getApplication(), "PATCH", this.uri, param, image);
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
                    StripeAccountInfoObject saio = new StripeAccountInfoObject(jo);
                    if (saio.individual.verification.document.front != null) {
                        replaceCardFront.setVisibility(View.VISIBLE);
                        cardFront.setVisibility(View.GONE);
                    }
                    if (saio.individual.verification.document.back != null) {
                        replaceCardBack.setVisibility(View.VISIBLE);
                        cardBack.setVisibility(View.GONE);
                    }
                }
            }
            if (this.param.equals("front")){
                isFront = false;
            } else if (this.param.equals("back")){
                isBack = false;
            }
            showUploadProgress(false, param);
            super.onPostExecute(response);
        }
    }

    void showUploadProgress(boolean show, String param){
        if (param.equals("front")){
            frontProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            imageFront.setVisibility(show ? View.GONE : View.VISIBLE);
        } else if (param.equals("back")){
            backProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            imageBack.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_TAKE_PHOTO:
                    Uri photoUri = Uri.fromFile(photoFile);
                    if (photoUri != null) {
                        if (isFront){
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                                uploadImage(bitmap, "front");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (isBack){
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                                uploadImage(bitmap, "back");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
            }
        }
    }
    void showNoAccessNotification(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EX_STORAGE_AND_CAMERA);
                })
                .create()
                .show();
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    File photoFile;
    static final int REQUEST_TAKE_PHOTO = 2;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.comeaqui.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode){
            case READ_EX_STORAGE_AND_CAMERA:
                if (
                        grantResults[0] != PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    showNoAccessNotification("Camera and storage access",
                            "In order to obtain best quality images we need access to camera and external storage");
                } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showNoAccessNotification("Camera access","In order to capture images we need access to camera");
                } else if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    showNoAccessNotification("Storage access", "In order to obtain best quality images we need access external storage");
                } else {
                    dispatchTakePictureIntent();
                }
                return;
        }
    }

    private static final int READ_EX_STORAGE_AND_CAMERA = 3;
    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||  ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showNoAccessNotification("Camera and storage access",
                        "In order to obtain best quality images we need access to camera and external storage");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                showNoAccessNotification("Camera access","In order to capture images we need access to camera");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                showNoAccessNotification("Storage access", "In order to obtain best quality images we need access external storage");
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EX_STORAGE_AND_CAMERA);
            }
        } else {
            dispatchTakePictureIntent();
        }
    }
}