package com.baptisteamato.myapplication;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baptisteamato.myapplication.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class AsyncHttpPost extends AsyncTask<String,Void,Integer> {

    private Context mContext;

    public AsyncHttpPost (Context context){
        mContext = context;
    }

    @Override
    protected Integer doInBackground(String... params) {

        // Create a new HttpClient and Post Header
        String url = mContext.getResources().getString(R.string.api_envoi);
        //set connection timeout to 3 seconds
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
        HttpConnectionParams.setSoTimeout(httpParameters, 3000);
        //add parameter
        HttpClient httpclient = new DefaultHttpClient(httpParameters);

        HttpPost httppost = new HttpPost(url);

        int responseCode = 500;

        try {
            // Execute HTTP Post Request
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("idStore", params[0]));
            if (params[0].equals("0"))
                nameValuePairs.add(new BasicNameValuePair("storeName", params[1]));
            nameValuePairs.add(new BasicNameValuePair("login", params[2]));
            nameValuePairs.add(new BasicNameValuePair("rating", params[3]));
            nameValuePairs.add(new BasicNameValuePair("commentary", params[4]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httppost.setHeader(mContext.getResources().getString(R.string.api_key_name),mContext.getResources().getString(R.string.api_key_value));
            HttpResponse httpResponse = httpclient.execute(httppost);
            responseCode = httpResponse.getStatusLine().getStatusCode();

        } catch (ConnectTimeoutException e) {
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
        return responseCode;
    }
}
