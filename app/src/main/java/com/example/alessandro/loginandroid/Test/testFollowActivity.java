package com.example.alessandro.loginandroid.Test;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.alessandro.loginandroid.Activity.ListUser;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class testFollowActivity extends Activity implements View.OnClickListener {
    ImageButton buttonHOME, buttonSEARCH, buttonFOLLOW, buttonPROFILE;

    private Button followContactButton;
    private Button followMessageButton;
    ArrayList<User> users;
    ClientLocalStore clientLocalStore;

    private ListView followListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_follow);

        buttonHOME = (ImageButton) findViewById(R.id.buttonHOME);
        buttonSEARCH = (ImageButton) findViewById(R.id.buttonSEARCH);
        buttonFOLLOW = (ImageButton) findViewById(R.id.buttonFOLLOW);
        buttonPROFILE = (ImageButton) findViewById(R.id.buttonPROFILE);

        buttonHOME.setOnClickListener(this);
        buttonSEARCH.setOnClickListener(this);
        buttonFOLLOW.setOnClickListener(this);
        buttonPROFILE.setOnClickListener(this);

        followContactButton = (Button) findViewById(R.id.followContactButton);
        followMessageButton = (Button) findViewById(R.id.followMessageButton);

        followContactButton.setOnClickListener(this);
        followMessageButton.setOnClickListener(this);
        followListView = (ListView)findViewById(R.id.followListView);

        users = new ArrayList<>();

        clientLocalStore = new ClientLocalStore(this);
        new GetFolloweedTask(clientLocalStore.getUser()).execute();
        ListUser adapter = new ListUser(this, users);
        followListView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.buttonHOME):
                Intent intent = new Intent(this, testMainActivity.class);
                startActivity(intent);
                break;
            case (R.id.buttonSEARCH):
                Intent intent1 = new Intent(this, testSearchActivity.class);
                startActivity(intent1);

                break;
            case (R.id.buttonFOLLOW):
                Intent intent2 = new Intent(this, testFollowActivity.class);
                startActivity(intent2);
                break;
            case (R.id.buttonPROFILE):
                Intent intent3 = new Intent(this, testProfileActivity.class);
                startActivity(intent3);
                break;
            case R.id.followContactButton:
                break;
            case R.id.followMessageButton:
                break;
        }

    }

    public class GetFolloweedTask extends AsyncTask<Void,Void,Void>{
        private User user;
        public GetFolloweedTask(User user){this.user=user;}
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/getFollowed");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("id_user", Integer.toString(user.getId_user())));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity =response.getEntity();
                String json = EntityUtils.toString(entity);
                JSONArray usersArray = new JSONArray(json);
                System.out.println(json);

                for (int i = 0; i < usersArray.length(); i++) {

                    User user = new Gson().fromJson(usersArray.get(i).toString(), User.class);
                    users.add(user);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}