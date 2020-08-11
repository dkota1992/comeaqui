package com.comeaqui.eduardorodriguez.comeaqui.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.message.BasicNameValuePair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class ServerAPI {

    public static String get(Context context, String url) throws IOException {

        String credentials = getCredentials(context);

        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            connection.connect();
            int responseCode = connection.getResponseCode();
//            if (responseCode != HttpsURLConnection.HTTP_OK) {
//                throw new IOException("HTTP error code: " + responseCode);
//            }

            if (responseCode >= 400) {
                return readStream(connection.getErrorStream());
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public static String getNoCredentials(String url) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
//            if (responseCode != HttpsURLConnection.HTTP_OK) {
//                throw new IOException("HTTP error code: " + responseCode);
//            }

            if (responseCode >= 400) {
                return readStream(connection.getErrorStream());
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public static String upload(Context context, String method, String url, String[][] params) throws IOException {

        String credentials = getCredentials(context);
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + credentials);

            List<NameValuePair> paramsArr = new ArrayList<>();
            for (String[] ss: params){
                paramsArr.add(new BasicNameValuePair(ss[0], ss[1]));
            }
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(paramsArr));
            writer.flush();
            writer.close();
            os.close();
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST) {
                return readStream(connection.getErrorStream());
            }
            if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                return readStream(connection.getErrorStream());
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public static String uploadNoCredentials(String method, String url, String[][] params) throws IOException {

        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            List<NameValuePair> paramsArr = new ArrayList<>();
            for (String[] ss: params){
                paramsArr.add(new BasicNameValuePair(ss[0], ss[1]));
            }
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(paramsArr));
            writer.flush();
            writer.close();
            os.close();
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST) {
                return readStream(connection.getErrorStream());
            }
            if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                return readStream(connection.getErrorStream());
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public static String delete(Context context, String url) throws IOException {

        String credentials = getCredentials(context);

        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("DELETE");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            connection.connect();
            int responseCode = connection.getResponseCode();
//            if (responseCode != HttpsURLConnection.HTTP_OK) {
//                throw new IOException("HTTP error code: " + responseCode);
//            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public static String uploadImage(Context context, String method, String url, String param, Bitmap imageBitmap) throws IOException {
        String credentials = getCredentials(context);

        String boundary = "-------------" + System.currentTimeMillis();
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            connection.setRequestProperty("Connection", "Keep-Alive");

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setBoundary(boundary);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            entityBuilder.addPart(param, new ByteArrayBody(imageBytes, "ANDROID.png"));
            HttpEntity reqEntity = entityBuilder.build();
            connection.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
            connection.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());

            OutputStream os = connection.getOutputStream();
            reqEntity.writeTo(connection.getOutputStream());
            os.close();
            connection.connect();

            int responseCode = connection.getResponseCode();
//            if (responseCode != HttpsURLConnection.HTTP_OK) {
//                throw new IOException("HTTP error code: " + responseCode);
//            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }


    private static String getCredentials(Context context){
        SharedPreferences pref = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            return pref.getString("cred", "");
        }
        return null;
    }
    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String readStream(InputStream stream) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        StringBuffer buffer = new StringBuffer();
        String line;
        BufferedReader bufferedReader = new BufferedReader(reader);
        while ((line = bufferedReader.readLine()) != null)
        {
            buffer.append(line);
        }
        return buffer.toString();
    }
}
