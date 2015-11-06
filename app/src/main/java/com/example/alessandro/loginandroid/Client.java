package com.example.alessandro.loginandroid;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 06/11/15.
 */
public class Client {
    User currentUser;
    String accessToken, refreshToken, RANDOM_ID, SECRET_ID, GRANT_TYPES;

    public Client(User currentUser, String accessToken, String refreshToken, String GRANT_TYPES) {
        this.currentUser = currentUser;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.RANDOM_ID = "ciao";
        this.SECRET_ID = "ciao";
        this.GRANT_TYPES = GRANT_TYPES;
    }

    public Client(){}


    public void login(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://localhost:4567/authorization");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("random_id", this.RANDOM_ID));
            nameValuePairs.add(new BasicNameValuePair("secret_id", this.SECRET_ID));
            nameValuePairs.add(new BasicNameValuePair("grant_types", this.GRANT_TYPES));
            nameValuePairs.add(new BasicNameValuePair("email", this.currentUser.getEmail()));
            nameValuePairs.add(new BasicNameValuePair("password", this.currentUser.getEmail()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httppost.setHeader("Authorization",this.accessToken);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            System.out.println(response);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }
}
