package com.comeaqui.eduardorodriguez.comeaqui.review;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.PaymentMethodObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.ReviewObject;

import com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment.PaymentMethodsActivity;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import static com.comeaqui.eduardorodriguez.comeaqui.R.color.colorPrimaryLight;
import static com.comeaqui.eduardorodriguez.comeaqui.R.color.grey_light;
import static com.comeaqui.eduardorodriguez.comeaqui.R.color.secondary_text_default_material_light;


public class ReviewHostActivity extends AppCompatActivity implements StarReasonFragment.OnFragmentInteractionListener {

    LinearLayout rateMealView;
    TextView posterRating;
    Button submitButton;
    ScrollView scrollView;
    View progress;

    TextView tip;
    EditText tipEText;
    TextView customTip;
    LinearLayout tipPercentages;
    boolean percentageTipOn = true;

    LinearLayout paymentMethod;
    ImageView cardIcon;
    TextView cardNumbers;
    Button changePaymentMethod;

    int tip_data;
    int tipPercentageOption;
    int[] percentage = {0, 15, 20, 25};


    OrderObject orderObject;
    int rating = 5;
    boolean[] reasonB = {false, false, false, false};
    String review = "";

    boolean readyForSubmit = false;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        TextView posterName = findViewById(R.id.poster_name);
        TextView amount = findViewById(R.id.amount);
        TextView cardLastNumbers = findViewById(R.id.card_last_numbers);
        rateMealView = findViewById(R.id.rate_meal_view);
        posterRating = findViewById(R.id.poster_rating);
        submitButton = findViewById(R.id.submitButton);
        scrollView = findViewById(R.id.scrollView);
        progress = findViewById(R.id.review_progress);

        tip = findViewById(R.id.priceView);
        tipEText = findViewById(R.id.price);
        customTip = findViewById(R.id.custom_tip);
        tipPercentages = findViewById(R.id.tip_percentages);

        paymentMethod = findViewById(R.id.payment_method_layout);
        cardIcon = findViewById(R.id.card_icon);
        cardNumbers = findViewById(R.id.card_last_numbers);
        changePaymentMethod = findViewById(R.id.change_payment);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("order") != null) {
            orderObject = (OrderObject) b.get("order");

            if(!orderObject.post.owner.profile_photo.contains("no-image")) {
                ImageView posterImage = findViewById(R.id.poster_image);
                Glide.with(this).load(orderObject.poster.profile_photo).into(posterImage);
            }

            blockSubmitButton(true);

            posterName.setText(orderObject.poster.first_name);
            amount.setText(orderObject.price_to_show);
            cardLastNumbers.setText("USER CARD");
            posterRating.setText(orderObject.owner.rating + "");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.star_reason_frame, StarReasonFragment.newInstance())
                    .commit();

            getMyChosenCard();
            setTipEText();
        }

        changePaymentMethod.setOnClickListener(v -> {
            Intent paymentMethod = new Intent(this, PaymentMethodsActivity.class);
            paymentMethod.putExtra("changeMode", true);
            startActivity(paymentMethod);
        });

        tipEText.setFocusableInTouchMode(false);
        tipEText.setFocusable(false);
        customTip.setOnClickListener(v -> {
            percentageTipOn = !percentageTipOn;
            if (percentageTipOn){
                hideKeyboard();
                tip_data = percentage[tipPercentageOption] * orderObject.post.price / 100;
                tip.setText("$" + String.format("%.02f", tip_data / 100.d));
            } else {
                tipEText.setText("");
                tip_data = 0;
                tip.setText("$" + String.format("%.02f", tip_data / 100.d));
            }
            tipPercentages.setVisibility(percentageTipOn ? View.VISIBLE: View.GONE);
            tipEText.setFocusableInTouchMode(!percentageTipOn);
            tipEText.setFocusable(!percentageTipOn);
            customTip.setText(percentageTipOn ? "Custom tip": "Percentage tip");
        });

        setTipButtons();
    }

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void setTipEText(){
        tipEText.setOnClickListener(v -> tipEText.setSelection(tipEText.getText().length()));
        tipEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count, int after){
                tipEText.setSelection(tipEText.getText().length());
            }
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before, int count){
                tip.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape));
                blockSubmitButton(false);
                if (s.length() > 0) {
                    int intText = Integer.parseInt(s.toString());
                    tip_data = intText;
                    tip.setText("$" + String.format("%.02f", tip_data / 100.d));
                } else {
                    tip.setText("$0.00");
                }
                tipEText.setSelection(tipEText.getText().length());
            }
            @Override
            public void afterTextChanged ( final Editable s){
                tipEText.setSelection(tipEText.getText().length());
            }
        });

        final View activityRootView = findViewById(R.id.root_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(getApplication(), 200)) {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        });
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    void blockSubmitButton(boolean block){
        submitButton.setAlpha(block ? 0.5f: 1);
        submitButton.setOnClickListener(v -> {
            if (!block){
                if (readyForSubmit){
                    submit();
                } else {
                    submitButton.setAlpha(0.5f);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                    submitButton.setText("SUBMIT");
                    rateMealView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    void submit(){
        tasks.add(new PostAsyncTask(getResources().getString(R.string.server) + "/create_review/").execute(
                new String[]{"order_id", "" + orderObject.id},
                new String[]{"review", review},
                new String[]{"rating", "" + rating},
                new String[]{"star_reason", ""},
                new String[]{"tip", tip_data + ""}
        ));
    }
    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;

        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            submitButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
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
            if (null != response){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                ReviewObject reviewObject = new ReviewObject(jo);
                finish();
            }
            submitButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }
    }

    void setTipButtons(){
        Button[] tipViews = new Button[]{
                findViewById(R.id.tip0),
                findViewById(R.id.tip1),
                findViewById(R.id.tip2),
                findViewById(R.id.tip3)
        };

        for (int i = 0; i < tipViews.length; i++){
            Button button = tipViews[i];
            final int iFinal = i;
            button.setOnClickListener(v -> {
                tipPercentageOption = iFinal;
                tip_data = percentage[tipPercentageOption] * orderObject.order_price / 100;
                tip.setText("$" + String.format("%.02f", tip_data / 100.d));
                blockSubmitButton(false);
                submitButton.setAlpha(1);
                for (Button tip: tipViews){
                    button.setBackgroundColor(ContextCompat.getColor(this, colorPrimaryLight));
                    button.setTextColor(Color.WHITE);
                }
                for (Button button2: tipViews){
                    if (button2 != button){
                        button2.setBackgroundColor(ContextCompat.getColor(this, grey_light));
                        button2.setTextColor(ContextCompat.getColor(this, secondary_text_default_material_light));
                    }
                }
            });
        }
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
                        PaymentMethodObject pm = new PaymentMethodObject(jo.get("data").getAsJsonArray().get(0).getAsJsonObject());
                        cardIcon.setImageDrawable(ContextCompat.getDrawable(getApplication(), pm.brandImage));
                        cardNumbers.setText(pm.last4.substring(pm.last4.length() - 4));
                    }
                }
            }
            super.onPostExecute(response);
        }
    }

    @Override
    public void onFragmentInteraction(int rating, boolean[] reasonB, String review) {
        submitButton.setAlpha(1);
        readyForSubmit = true;
        this.review = review;
    }

    @Override
    public void onHasToScrollIfNeeded() {
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onRating(int rating) {
        this.rating = rating;
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
