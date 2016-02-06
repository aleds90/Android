package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Adapters.ListUser;
import com.example.alessandro.loginandroid.Adapters.ListUserConversations;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Message;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.google.gson.Gson;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

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

public class RelationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long RIPPLE_DURATION = 250;


    ImageButton home, search, relation, profile;

    private Button contact;
    private Button message;
    ArrayList<User> users;
    ClientLocalStore clientLocalStore;
    ArrayList<Message> messages;
    private ListView userlist;
    ArrayList<User> sortedusers;

    ImageView hamburger;
    Toolbar toolbar;
    FrameLayout root;
    TextView logout,delete,settings,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relation_activity);
        createToolbar();

        home = (ImageButton) findViewById(R.id.home);
        search = (ImageButton) findViewById(R.id.search);
        relation = (ImageButton) findViewById(R.id.relation);
        profile = (ImageButton) findViewById(R.id.profile);

        home.setOnClickListener(this);
        search.setOnClickListener(this);
        relation.setOnClickListener(this);
        profile.setOnClickListener(this);

        contact = (Button) findViewById(R.id.follow_contact_btn);
        message = (Button) findViewById(R.id.follow_message_btn);

        contact.setOnClickListener(this);
        message.setOnClickListener(this);
        userlist = (ListView)findViewById(R.id.follow_userlist_lv);

        users = new ArrayList<>();
        clientLocalStore = new ClientLocalStore(this);
        new GetFolloweedTask(clientLocalStore.getUser()).execute();


    }

    private void createToolbar() {
        root=(FrameLayout)findViewById(R.id.root);
        toolbar = (Toolbar)findViewById(R.id.toolbar_hamburger);
        hamburger=(ImageView)findViewById(R.id.content_hamburger);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }
        View navigationMenu = LayoutInflater.from(this).inflate(R.layout.navigation,null);
        root.addView(navigationMenu);
        new GuillotineAnimation.GuillotineBuilder(navigationMenu, navigationMenu.findViewById(R.id.guillotine_hamburger), hamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {

                    @Override
                    public void onGuillotineOpened() {
                        //LOGOUT BUTTON
                        logout = (TextView) findViewById(R.id.logout_textview);
                        logout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clientLocalStore.clearClient();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        //DELETE BUTTON
                        delete = (TextView) findViewById(R.id.delete_textview);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //new deleteTask(clientLocalStore.getUser()).execute();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        //SETTING BUTTON
                        settings = (TextView) findViewById(R.id.setting_textview);
                        settings.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                                startActivity(intent);
                            }
                        });
                        //STATUS BUTTON
                        status = (TextView) findViewById(R.id.status_textview);
                        if (clientLocalStore.getUser().isActive()) {
                            status.setText("STATUS: DISPONIBILE");
                        } else {
                            status.setText("STATUS: NON DISPONIBILE");
                        }
                        status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (status.getText().toString().equals("STATUS: DISPONIBILE")) {
                                    status.setText("STATUS: NON DISPONIBILE");
                                    User user = clientLocalStore.getUser();
                                    user.setActive(false);
                                    new UpdateStatusTask(user).execute();
                                } else {
                                    status.setText("STATUS: DISPONIBILE");
                                    User user = clientLocalStore.getUser();
                                    user.setActive(true);
                                    new UpdateStatusTask(user).execute();
                                }

                            }
                        });


                    }

                    @Override
                    public void onGuillotineClosed() {

                    }

                })
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.home):
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case (R.id.search):
                Intent intent1 = new Intent(this, SearchActivity.class);
                startActivity(intent1);

                break;
            case (R.id.relation):
                Intent intent2 = new Intent(this, RelationActivity.class);
                startActivity(intent2);
                break;
            case (R.id.profile):
                Intent intent3 = new Intent(this, ProfileActivity.class);
                startActivity(intent3);
                break;
            case R.id.follow_contact_btn:
                new GetFolloweedTask(clientLocalStore.getUser()).execute();
                ListUser adapter = new ListUser(this, users);
                userlist.setAdapter(adapter);
                break;
            case R.id.follow_message_btn:
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

        userlist.setAdapter(luc);
        userlist.deferNotifyDataSetChanged();
    }

    public class GetFolloweedTask extends AsyncTask<Void,Void,Void>{
        private User user;
        JSONArray usersArray;
        public GetFolloweedTask(User user){this.user=user;}

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            users = new ArrayList<>();
            for (int i = 0; i < usersArray.length(); i++) {

                User user = null;
                try {
                    user = new Gson().fromJson(usersArray.get(i).toString(), User.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                users.add(user);
            }
            ListUser adapter = new ListUser(getApplicationContext(), users);
            userlist.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/getFollowed");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
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

    public class UpdateStatusTask extends AsyncTask<Void, Void, Void> {

        private User user;

        public UpdateStatusTask(User user) {
            this.user = user;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            clientLocalStore.updateUser(user);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/updateStatus");

                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("status", Boolean.toString(user.isActive())));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);
                System.out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}