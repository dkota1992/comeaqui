package com.comeaqui.eduardorodriguez.comeaqui.login_and_register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.*;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;

import com.comeaqui.eduardorodriguez.comeaqui.PrepareActivity;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.forgot_password.ForgotPasswordActivity;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager.LoaderCallbacks;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     */
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private TextView mPasswordViewVal;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        context = getApplicationContext();
        mEmailView = findViewById(R.id.senderEmail);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPasswordViewVal = findViewById(R.id.password_val_text);

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        findViewById(R.id.forgot_password).setOnClickListener((v) -> {
            Intent a = new Intent(this, ForgotPasswordActivity.class);
            startActivity(a);
        });
        attemptAutoLogin();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS));
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptAutoLogin(){
        SharedPreferences pref = getSharedPreferences("Login", Context.MODE_PRIVATE);
        String email = pref.getString("email", null);
        String password = pref.getString("password", null);
        if (email != null && password != null){
            signInServerAndFirebase(email, password);
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.

        // Store values at the start_time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            signInServerAndFirebase(email, password);
        }
    }

    private void signInServerAndFirebase(String email, String password){
        showProgress(true);

        mAuthTask = new UserLoginTask(email, password);
        try {
            goToMain(mAuthTask.execute((Void) null).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
//            if (!task.isSuccessful()){
//                Toast.makeText(LoginActivity.this, "Firebase sign in problem", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void goToMain(boolean success){
        mAuthTask = null;
        if (success) {
            Intent k = new Intent(LoginActivity.this, PrepareActivity.class);
            showProgress(false);
            startActivity(k);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up);
        }
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve foodPostHashMap rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email.toLowerCase();
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPasswordViewVal.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getResources().getString(R.string.server) + "/login/");

            String cred = mEmail + ":" + mPassword;
            httpGet.addHeader("Authorization", "Basic " + Base64.encodeToString(cred.getBytes(), Base64.NO_WRAP));
            httpGet.setHeader("Content-Type", "application/json");
            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    String credentials = mEmail + ":" + mPassword;

                    SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                    String authorization = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("cred", authorization);
                    editor.putBoolean("signed_in", true);
                    editor.putString("username", mEmail);
                    editor.putString("password", mPassword);
                    editor.apply();

                    return true;
                } else {
                    mPasswordViewVal.setText("Wrong (email/username)/password or email not confirmed yet");
                    mPasswordViewVal.setVisibility(View.VISIBLE);

//                    InputStream stream = response.getEntity().getContent();
//                    if (stream != null) {
//                        String r = ServerAPI.readStream(stream);
//                        JsonObject jo = new JsonParser().parse(r).getAsJsonObject();
//                        if (jo.get("error_message") != null) {
//                            if (jo.get("error_message").getAsJsonObject().get("email") != null){
//                                mEmailViewVal.setText(jo.get("error_message").getAsJsonObject().get("email").getAsString());
//                            }
//                            if (jo.get("error_message").getAsJsonObject().get("password") != null){
//
//                            }
//                        }
//                    }
                    return false;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

