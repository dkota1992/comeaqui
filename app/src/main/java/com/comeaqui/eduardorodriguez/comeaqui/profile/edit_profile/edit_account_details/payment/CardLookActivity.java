package com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.PaymentMethodObject;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class CardLookActivity extends AppCompatActivity {

    TextView cardNumber;
    TextView expiryDate;
    TextView brand;
    ImageButton options;

    String cardId;
    PaymentMethodObject card;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_look);

        cardNumber = findViewById(R.id.card_number);
        expiryDate = findViewById(R.id.expiry_date);
        options = findViewById(R.id.options);
        brand = findViewById(R.id.brand);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("cardId") != null) {
            cardId = b.getString("cardId");
            getCard();
        }

        options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenu().add("Set as default");
            popupMenu.getMenu().add("Delete");

            popupMenu.setOnMenuItemClickListener(item -> {
                setOptionsActions(item.getTitle().toString());
                return true;
            });
            popupMenu.show();
        });
    }

    void setView(){
        cardNumber.setText("****" + card.last4.substring(card.last4.length() - 4));
        expiryDate.setText(card.exp_month + "/" + card.exp_year);
        brand.setText((card.brand == null) ? "Card" : card.brand);
    }

    void setOptionsActions(String title){
        switch (title){
            case "Set as default":
                setAsDefaultPayment();
                break;
            case "Delete":
                deleteCard();
                break;
        }
    }
    void setAsDefaultPayment(){
        tasks.add(new PatchAsyncTask(getResources().getString(R.string.server) + "/select_as_payment_method/" + cardId + "/").execute());
    }
    private class PatchAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public PatchAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
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
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    Toast.makeText(getApplication(), "Payment set as default", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplication(), jo.get("error_message").getAsString(), Toast.LENGTH_SHORT).show();
                }
            }
            super.onPostExecute(response);
        }
    }

    void deleteCard(){
        tasks.add(new DeletePostAsyncTask(getResources().getString(R.string.server) + "/card_detail/" + cardId + "/").execute());
    }

    class DeletePostAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public DeletePostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.delete(getApplicationContext(), this.uri);
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
                    Toast.makeText(getApplication(), "Payment deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplication(), jo.get("error_message").getAsString(), Toast.LENGTH_SHORT).show();
                }
            }
            super.onPostExecute(response);
        }
    }

    void getCard(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/card_detail/" + cardId + "/").execute());
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
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    card = new PaymentMethodObject(new JsonParser().parse(response).getAsJsonObject());
                    setView();
                } else {
                    Toast.makeText(getApplication(), jo.get("error_message").getAsString(), Toast.LENGTH_SHORT).show();
                }
            }
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
