package com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;

import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import java.io.IOException;
import java.util.ArrayList;

public class CreditCardInformationActivity extends AppCompatActivity {

    EditText creditCardView;
    EditText expMonth;
    EditText expYear;
    EditText cvcView;

    TextView numberVal;
    TextView monthVal;
    TextView yearVal;
    TextView cvcVal;
    TextView errorMessage;

    ProgressBar mProgressView;
    Button saveCardButtonView;

    String cardType = "";
    String currentS = "";
    String card_number = "";

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_information);

        creditCardView = findViewById(R.id.creditCard);
        expMonth = findViewById(R.id.exp_month);
        expYear = findViewById(R.id.exp_year);
        cvcView = findViewById(R.id.cvc);
        errorMessage = findViewById(R.id.error_message);
        mProgressView = findViewById(R.id.progressBar);
        saveCardButtonView = findViewById(R.id.saveCardButton);

        numberVal = findViewById(R.id.number_val);
        monthVal = findViewById(R.id.month_val);
        yearVal = findViewById(R.id.year_val);
        cvcVal = findViewById(R.id.cvc_val);

        saveCardButtonView.setOnClickListener(v -> {
            PostAsyncTask post = new PostAsyncTask(getResources().getString(R.string.server) + "/card/");
            tasks.add(post.execute(
                        new String[]{"card_number", card_number, ""},
                        new String[]{"exp_month", expMonth.getText().toString(), ""},
                        new String[]{"exp_year", expYear.getText().toString(), ""},
                        new String[]{"cvc", cvcView.getText().toString(), ""}
                        ));
        });

        creditCardView.setOnClickListener(v -> creditCardView.setSelection(creditCardView.getText().length()));
        creditCardView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String st = s.toString();
                if (!currentS.equals(st)){
                    String st_r = st.replaceAll("[^0-9]", "");
                    StringBuilder s_to_show = new StringBuilder();
                    for (int i = 0; i < st_r.length(); i++){
                        if (i % 4 == 0){
                            s_to_show.append(" ");
                        }
                        s_to_show.append(st_r.charAt(i));
                    }
                    card_number = st_r;
                    currentS = s_to_show.toString();
                    creditCardView.setText(s_to_show.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                creditCardView.setSelection(creditCardView.getText().length());
            }
        });

        setEditText(creditCardView, numberVal);
        setEditText(expMonth, monthVal);
        setEditText(expYear, yearVal);
        setEditText(cvcView, cvcVal);
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
    void showValtext(TextView tv, String text, EditText et){
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
        et.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
    }

    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
        String uri;

        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            showProgress(true);
            errorMessage.setVisibility(View.GONE);
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
            if (response != null){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (jo.get("error_message") == null){
                    finish();
                } else {
                    String e_message = jo.get("error_message").getAsString();
                    if (e_message.equals("number")){
                        showValtext(numberVal, "Invalid number", creditCardView);
                    }
                    if (e_message.equals("exp_month")){
                        showValtext(monthVal, "Invalid month", expMonth);
                    }
                    if (e_message.equals("exp_year")){
                        showValtext(yearVal, "Invalid year", expYear);
                    }
                    if (e_message.equals("cvc")){
                        showValtext(cvcVal, "Invalid cvc", cvcView);
                    }
                }
            }
            showProgress(false);
            super.onPostExecute(response);
        }
    }

    void showProgress(boolean show){
        if (show){
            mProgressView.setVisibility(View.VISIBLE);
            saveCardButtonView.setVisibility(View.GONE);
        } else {
            mProgressView.setVisibility(View.GONE);
            saveCardButtonView.setVisibility(View.VISIBLE);
        }
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, 10);
    }

    /**
     * Formatting a credit card number: #### #### #### #######
     */
    public static class CreditCardNumberFormattingTextWatcher implements TextWatcher {

        private boolean lock;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (lock || s.length() > 16) {
                return;
            }
            lock = true;
            for (int i = 4; i < s.length(); i += 5) {
                if (s.toString().charAt(i) != ' ') {
                    s.insert(i, " ");
                }
            }
            lock = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                creditCardView.setText(scanResult.getRedactedCardNumber());
                card_number = scanResult.getRedactedCardNumber();
                cardType = (scanResult.getCardType() != null) ? scanResult.getCardType() + "" : " ";

                resultDisplayStr += "Card Type: " + scanResult.getCardType() + "\n";
                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                    String exYear = Integer.toString(scanResult.expiryYear);
                    creditCardView.setText(scanResult.expiryMonth + "/" + exYear.substring(2));
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                    cvcView.setText(scanResult.cvv);
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
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
