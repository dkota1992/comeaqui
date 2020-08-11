package com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.PaymentMethodObject;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class PaymentMethodsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    boolean changeMode;
    PaymentMethodsAdapter adapter;
    ArrayList<PaymentMethodObject> data;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        TextView addPaymentMethod = findViewById(R.id.add_payment_method);
        ImageView back = findViewById(R.id.back_arrow);

        recyclerView = findViewById(R.id.payment_methods_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentMethodsAdapter(this, data);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("changeMode") != null) {
            changeMode = b.getBoolean("changeMode");
        }

        addPaymentMethod.setOnClickListener(v -> {
            Intent pm = new Intent(this, CreditCardInformationActivity.class);
            startActivity(pm);
        });

        back.setOnClickListener((v) -> finish());
    }

    @Override
    protected void onResume() {
        getCardPaymentMethods();
        super.onResume();
    }

    void getCardPaymentMethods(){
        GetAsyncTask process = new GetAsyncTask(getResources().getString(R.string.server) + "/my_payment_methods/");
        tasks.add(process.execute());
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
            if (response != null) {
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    makeList(jo.get("data").getAsJsonArray());
                }
            }
            super.onPostExecute(response);
        }
    }

    void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new PaymentMethodObject(jo));
            }
            adapter = new PaymentMethodsAdapter(this, data);
            recyclerView.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void onPaymentMethodClicked(PaymentMethodObject paymentMethodObject){
        if (changeMode){
            selectAsDefaultPayment(paymentMethodObject.id);
        } else {
            Intent c = new Intent(this, CardLookActivity.class);
            c.putExtra("cardId", paymentMethodObject.id);
            startActivity(c);
        }
    }
    void selectAsDefaultPayment(String paymentMethodId){
        tasks.add(new PatchAsyncTask(getResources().getString(R.string.server) + "/select_as_payment_method/" + paymentMethodId + "/").execute());
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
                    getCardPaymentMethods();
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
