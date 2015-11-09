package com.example.alessandro.loginandroid.Entity;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



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
        new LoginTask(this).execute();
    }

    public class LoginTask extends AsyncTask<String, Void, Void>{

        private Client client;
        public LoginTask(Client client){
            this.client=client;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/authorization");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("random_id", client.RANDOM_ID));
                nameValuePairs.add(new BasicNameValuePair("secret_id", client.SECRET_ID));
                nameValuePairs.add(new BasicNameValuePair("grant_types", client.GRANT_TYPES));
                nameValuePairs.add(new BasicNameValuePair("email", client.currentUser.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("password", client.currentUser.getEmail()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                httppost.setHeader("Authorization", client.accessToken);
                //httppost.setHeader("Content-type", "application/json");
                //httppost.setHeader("Accept","application/json");

                // Execute HTTP Post Request
//                HttpResponse response = null;
//
//                response = httpclient.execute(httppost);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                JSONObject jsonObject = new JSONObject(json);
                //jsonObject.getString("access_Token");
                jsonObject.getString("type");

                //Response responseServer = new Gson().fromJson(jsonObject);
                System.out.println(json);

            }
            catch (Exception e){
                System.out.println("sono fuori");
                e.printStackTrace();
            }
            return null;
        }
    }
}