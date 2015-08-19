package com.baptisteamato.myapplication;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baptisteamato.myapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class AsyncHttpGet extends AsyncTask<String, String, String> {

    private Context mContext;

    public AsyncHttpGet (Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(String... serverUrl) {

        BufferedReader reader = null;
        StringBuilder stringBuilder = null;

        try {
            URL url = new URL(serverUrl[0]);    // ici, serverURL[0] est l'URL de connexion
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //envoi de la clé dans le Header
            urlConn.setRequestProperty(mContext.getResources().getString(R.string.api_key_name), mContext.getResources().getString(R.string.api_key_value));
            //set timeout to 3 seconds
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(5000);
            urlConn.connect();
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                stringBuilder = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
            }
        }catch (SocketTimeoutException e) {
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    return null;
                }
            }
        }
        return stringBuilder.toString();
    }
}
