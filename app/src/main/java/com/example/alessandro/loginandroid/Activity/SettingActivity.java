package com.example.alessandro.loginandroid.Activity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SettingActivity extends AppCompatActivity{

    private Button httpconn;
    private ClientLocalStore localstore;
    final static String URL_REQUEST = "http://njsao.pythonanywhere.com/other_profile_init";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        localstore = new ClientLocalStore(this);
        final User myUser = localstore.getUser();

        httpconn = (Button)findViewById(R.id.testhttpconn);
        httpconn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserTask(myUser, URL_REQUEST).execute();
            }
        });



    }
    public class UserTask extends AsyncTask<Void,Void,Void>{
        private HashMap<String,String> hashMap = new HashMap<String,String>();
        private User user;
        private String URL;
        private String response;
        public UserTask(User user, String URL){
            this.user = user;
            this.URL = URL;
        }

        @Override
        protected void onPreExecute() {
            hashMap.put("user_email", user.getEmail());
            hashMap.put("other_email", "b");

        }

        @Override
        protected Void doInBackground(Void... params) {
            response = performPostCall(URL,hashMap);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                JSONObject object = new JSONObject(response);
                System.out.println(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String  performPostCall(String requestURL,
                                   HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            System.out.println(responseCode);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;

                }
                System.out.println(response);

            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));

        }

        return result.toString();
    }

}