package com.comeaqui.eduardorodriguez.comeaqui.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class Server extends AsyncTask<String[], Void, String>{
    String uri;
    String method;

    private Context context;

    public Server(Context context, String method, String uri) {
        this.context = context;
        this.uri = uri;
        this.method = method;
    }

    public HttpURLConnection getConnection(String[]... params) throws IOException {

        String credentials = "";
        SharedPreferences pref = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            credentials = pref.getString("cred", "");
        }

        URL url = new URL(this.uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod(this.method);
        conn.setRequestProperty("Content-Type","multipart/form-foodPostHashMap");
        conn.setRequestProperty("Authorization", "Basic " + credentials);
        conn.setDoInput(true);
        conn.setDoOutput(true);


        if (params.length > 0) {
            String inputJsonString = cerateBodyString(params);
            byte[] outputInBytes = inputJsonString.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();
        }
        return conn;
    }

    @Override
    protected String doInBackground(String[]... params) {

        try{
            this.uri += cerateParams(params);
            HttpURLConnection conn = getConnection(params);
            conn.connect();

            InputStream instream = new BufferedInputStream(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    static String cerateParams(String[][] param) {
        StringBuilder str = new StringBuilder();
        for (String[] ss: param){
            if (ss[1] != null)
                str.append("&").append(ss[0]).append("=").append(ss[1]);
        }
        return str.toString();
    }

    static String cerateBodyString(String[][] param) {
        StringBuilder json = new StringBuilder("{");
        for (String[] ss: param){
            json.append("\"").append(ss[0]).append("\"").append(":").append("\"").append(ss[1]).append("\",");
        }
        String js = json.substring(0, json.length() - 1);
        js += "}";
        return js;
    }
}