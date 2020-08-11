package com.comeaqui.eduardorodriguez.comeaqui.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.Context.MODE_PRIVATE;

public class PutAsyncTask extends AsyncTask<String[], Void, JSONObject> {

    Bitmap imageBitmap;
    String uri;
    private Context context;
    public PutAsyncTask(Context context, String uri){
        this.context = context;
        this.uri = uri;
    }
    @Override
    protected JSONObject doInBackground(String[]... params)
    {
        String credentials = "";
        SharedPreferences pref = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            credentials = pref.getString("cred", "");
        }

        HttpPut httpPut = new HttpPut(this.uri);
        httpPut.addHeader("Authorization", "Basic " + credentials);

        HttpClient httpclient = new DefaultHttpClient();
        String boundary = "-------------" + System.currentTimeMillis();
        httpPut.setHeader("Content-type","multipart/form-foodPostHashMap; boundary="+boundary);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary);

        for (String[] ss: params){
            multipartEntityBuilder.addPart(ss[0], new StringBody(ss[1], ContentType.TEXT_PLAIN));
        }
        HttpEntity entity = multipartEntityBuilder.build();
        httpPut.setEntity(entity);
        try {
            HttpResponse response = httpclient.execute(httpPut);
            InputStream instream = response.getEntity().getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            return new JSONObject(stringBuffer.toString());

        }  catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject response)
    {
        if(response != null) {}
    }
}