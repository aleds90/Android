package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.alessandro.loginandroid.Adapters.ListUser;
import com.example.alessandro.loginandroid.Adapters.ListUserConversations;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Message;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class testFollowActivity extends Activity implements View.OnClickListener {
    ImageButton buttonHOME, buttonSEARCH, buttonFOLLOW, buttonPROFILE;

    private Button followContactButton;
    private Button followMessageButton;
    ArrayList<User> users;
    ClientLocalStore clientLocalStore;
    ArrayList<Message> messages;
    private ListView followListView;
    ArrayList<User> sortedusers;

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
                users = new ArrayList<>();
                clientLocalStore = new ClientLocalStore(this);
                new GetFolloweedTask(clientLocalStore.getUser()).execute();
                ListUser adapter = new ListUser(this, users);
                followListView.setAdapter(adapter);
                break;
            case R.id.followMessageButton:
                messages = new ArrayList<Message>();
                new MessageTask(clientLocalStore.getUser()).execute();

                break;
        }

    }

    private ArrayList<User> createSortedUserList(ArrayList<Message> lista) {
        ArrayList<User> usersb = new ArrayList<>();
        for(Message m : lista){
            if(m.getId_receiver().getId_user() !=  clientLocalStore.getUser().getId_user() &&
                    !isUserinList(usersb, m.getId_receiver())){
                usersb.add(m.getId_receiver());
                Log.d("LISTA FINALE", m.getId_receiver().getName());
            }
            else if(m.getId_sender().getId_user() !=  clientLocalStore.getUser().getId_user() &&
                   !isUserinList(usersb, m.getId_sender())){
                usersb.add(m.getId_sender());
                Log.d("LISTA FINALE", m.getId_sender().getName());

            }
        }
        return usersb;
    }

    public boolean isUserinList(ArrayList<User> l, User u){
        for(User a : l){
            if(a.getId_user() == u.getId_user()){
                return true;
            }
        }
        return false;
    }


    public class MessageTask extends AsyncTask<Void,Void,Void>{
        private User user;
        JSONArray usersArray;
        public MessageTask(User user){this.user=user;}

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int i = 0; i < usersArray.length(); i++) {
                Message message = null;
                try {
                    message = new Gson().fromJson(usersArray.get(i).toString(), Message.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                messages.add(message);
                Log.d("MESSAGGIO RIVEVUTO", message.getText());
            }
            sortedusers = createSortedUserList(messages);
            setAdapterInAsynk();



        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
                HttpConnectionParams.setSoTimeout(httpParams, 10000);
                HttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/getMessages");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("user_mail", user.getEmail()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity =response.getEntity();
                String json = EntityUtils.toString(entity);
                usersArray = new JSONArray(json);
                System.out.println(json);


            }catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void setAdapterInAsynk() {
        ListUserConversations luc= new ListUserConversations(this, sortedusers, messages);

        followListView.setAdapter(luc);
        followListView.deferNotifyDataSetChanged();
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