package com.example.baptisteamato.findutc;


import android.content.Context;
import android.os.AsyncTask;

import com.example.baptisteamato.findutc.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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
        HttpClient httpclient = new DefaultHttpClient();
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

        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
        return responseCode;
    }
}
