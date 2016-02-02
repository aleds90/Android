package com.example.alessandro.loginandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.alessandro.loginandroid.Adapters.ListUser;
import com.example.alessandro.loginandroid.Entity.Client;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Response;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.google.gson.Gson;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long RIPPLE_DURATION = 250;
    Toolbar toolbar;
    FrameLayout root;
    ImageButton home, search, relation, profile;
    ListView listViewUSERLIST;
    ClientLocalStore clientLocalStore;
    ArrayList<User> users;
    ImageView hamburger, messages;
    ListUser adapter;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    TextView logout,delete,settings,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        createViews();
        createToolbar();
        clientLocalStore = new ClientLocalStore(this);
        new UserListTask(clientLocalStore.getUser()).execute();
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new UserListTask(clientLocalStore.getUser()).execute();
            }
        });
    }

    /**
     * Metodo che setta tutte le views della Main Activity
     */
    private void createViews(){
        messages = (ImageView) findViewById(R.id.messages);
        listViewUSERLIST = (ListView) findViewById(R.id.listViewUSERLIST);

        home = (ImageButton) findViewById(R.id.home);
        search = (ImageButton) findViewById(R.id.search);
        relation = (ImageButton) findViewById(R.id.relation);
        profile = (ImageButton) findViewById(R.id.profile);
        home.setOnClickListener(this);
        search.setOnClickListener(this);
        relation.setOnClickListener(this);
        profile.setOnClickListener(this);
    }

    /**
     * Metodo che inizializza la toolbar superiore e tutti i suoi oggetti
     */
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
                                //// TODO: 28/01/2016 Abilitare il delete 
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
                        //EDIT BUTTON
                        TextView edit = (TextView)findViewById(R.id.edit_textview);
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                                startActivity(intent);
                            }
                        });

                        //STATUS BUTTON
                        status = (TextView) findViewById(R.id.status_textview);
                        if (clientLocalStore.getUser().isActive()) {
                            status.setText(getResources().getString(R.string.status_available));
                        } else {
                            status.setText(getResources().getString(R.string.status_not_available));
                        }
                        status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (status.getText().equals(getResources().getString(R.string.status_available))) {
                                    status.setText(getResources().getString(R.string.status_not_available));
                                    User user = clientLocalStore.getUser();
                                    user.setActive(false);
                                    new UpdateStatusTask(user).execute();
                                } else {
                                    status.setText(getResources().getString(R.string.status_available));
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

    /**
     * Metodo che gestisce tutti gli onClickListener della MainActivity
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case (R.id.home):
                i = new Intent(this, MainActivity.class);
                break;
            case (R.id.search):
                i = new Intent(this, SearchActivity.class);
                break;
            case (R.id.relation):
                i = new Intent(this, RelationActivity.class);
                break;
            case (R.id.profile):
                i = new Intent(this, ProfileActivity.class);
                break;
        }
        startActivity(i);
    }

    /**
     * Metodo utilizzato per identificare i token e settare il client
     */
    @Override
    protected void onStart() {
        super.onStart();
        //in particolare controlliamo se non abbiamo token veniamo indirizzati direttamente alla activity di login
        if (clientLocalStore.getClient().getRefreshToken().equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }
        // se abbiamo il token proviamo ad effettuare il login tramite refresh
        else {
            Client client = clientLocalStore.getClient();
            client.setGRANT_TYPES("Refresh");
            User user = clientLocalStore.getUser();
            new LoginTask(client, user).execute();
        }
    }

    /**
     * Classe che si occupa del login di refresh
     */
    public class LoginTask extends AsyncTask<String, Void, Void> {
        private Client client;
        private User user;

        public LoginTask(Client client, User user) {
            this.client = client;
            this.user = user;
        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://njsao.pythonanywhere.com/login");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("random_id", client.getRANDOM_ID()));
                nameValuePairs.add(new BasicNameValuePair("secret_id", client.getSECRET_ID()));
                nameValuePairs.add(new BasicNameValuePair("grant_types", client.getGRANT_TYPES()));
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httppost.setHeader("Authorization", client.getRefreshToken());
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println(json);
                Response responseServer = new Gson().fromJson(json, Response.class);
                handleResponse(responseServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Classe che restituisce un arraylist di utenti e che popola la ListView
     */
    public class UserListTask extends AsyncTask<Void, Void, Void> {
        private User user;
        private JSONArray usersArray;
        boolean checkMessage;
        String message;

        public UserListTask(User user) {
            this.user = user;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (checkMessage){
                messages.setVisibility(View.VISIBLE);
            }else{
                messages.setVisibility(View.INVISIBLE);
            }
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
            setAdapter();
            mWaveSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://njsao.pythonanywhere.com/get_users");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println(json);
                JSONObject jsonObject= new JSONObject(json);
                message = jsonObject.getString("message_count");
                usersArray = jsonObject.getJSONArray("users");
                int count = Integer.parseInt(message);
                checkMessage = count > 0;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Metodo per settare l'adapter della listview
     */
    private void setAdapter() {
        // tengo salvato il setting del vecchio adapter:
        //adapter = new ListUser(this, users);
        //listViewUSERLIST.setAdapter(adapter);

        FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();
        listViewUSERLIST.setAdapter(new ListFlipUser(this, users, settings));


        listViewUSERLIST.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User f = (User) listViewUSERLIST.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, OtherProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                passUserByIntent(intent, f);
                getApplicationContext().startActivity(intent);
            }
        });

    }

    private void passUserByIntent(Intent intent, User user) {
        intent.putExtra("id_user", user.getId_user());
        intent.putExtra("name", user.getName());
        intent.putExtra("cognome", user.getSurname());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("city", user.getCity());
        intent.putExtra("role", user.getRole());
        intent.putExtra("bday", user.getBday());
        intent.putExtra("rate", user.getRate());
        intent.putExtra("status", user.isActive());
        intent.putExtra("description", user.getDescription());
    }

    /**
     * Metodo che si occupa di gestire la risposta della loginTask
     * @param responseServer
     */
    private void handleResponse(Response responseServer) {
        switch (responseServer.getType()) {
            //case 4, login tramite refresh andato a buon fine. otteniamo due token nuovi che salviamo nel local store
            case "4":
                User user = clientLocalStore.getUser();
                Client client = new Client(responseServer.getAccess_Token(), responseServer.getRefresh_Token(), "");
                clientLocalStore.storeClientData(client, user);
                break;
            //login tramite refresh non andato a buon fine. Veniamo reinderizzati nella pagina di login
            case "401":
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Classe che si occupa dell'Update dello status quando viene cambiato nella NavigationBar
     */
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
                HttpPost httpPost = new HttpPost("http://njsao.pythonanywhere.com/update_status");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("active", Integer.toString(user.isActive()?1:0)));
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

    /**
     * Classe che si occupa del flipAdapter per la listView
     */
    class ListFlipUser extends BaseFlipAdapter<User> {


        private final int PAGES = 3;
        private int[] IDS_INTEREST = {R.id.interest_1, R.id.interest_2, R.id.interest_3, R.id.interest_4};

        public ListFlipUser(Context context, List<User> items, FlipSettings settings) {
            super(context, items, settings);
        }

        @Override
        public View getPage(int position, View convertView, ViewGroup parent, final User friend1, User friend2) {
            final FriendsHolder holder;

            if (convertView == null) {
                holder = new FriendsHolder();

                convertView         = getLayoutInflater().inflate(R.layout.friends_merge_page, parent, false);
                holder.leftAvatar   = (TextView) convertView.findViewById(R.id.first);
                holder.rightAvatar  = (TextView) convertView.findViewById(R.id.second);
                holder.infoPage     = getLayoutInflater().inflate(R.layout.friends_info, parent, false);
                holder.nickName     = (TextView) holder.infoPage.findViewById(R.id.nickname);
                holder.surname      = (TextView)holder.infoPage.findViewById(R.id.surname);




                for (int id : IDS_INTEREST)
                    holder.interests.add((TextView) holder.infoPage.findViewById(id));
                convertView.setTag(holder);
            } else {
                holder = (FriendsHolder) convertView.getTag();
            }

            switch (position) {
                // Merged page with 2 friends
                case 1:

                    holder.leftAvatar.setBackgroundResource(friend1.getDrawableAvatar(friend1.getAvatar()));
                    holder.leftAvatar.setText(friend1.getRole());

                    if (friend2 != null) {
                        holder.rightAvatar.setBackgroundResource(friend2.getDrawableAvatar(friend2.getAvatar()));
                        holder.rightAvatar.setText(friend2.getRole());

                    }
                    break;
                default:
                    fillHolder(holder, position == 0 ? friend1 : friend2);
                    holder.infoPage.setTag(holder);
                    return holder.infoPage;
            }
            return convertView;
        }

        @Override
        public int getPagesCount() {
            return PAGES;
        }
        private void fillHolder(FriendsHolder holder, User friend) {
            if (friend == null)
                return;
            Iterator<TextView> iViews = holder.interests.iterator();
            Iterator<String> iInterests = friend.getInterests().iterator();
            while (iViews.hasNext() && iInterests.hasNext())
                iViews.next().setText(iInterests.next());
            final User currentUser = friend;
            System.out.println(currentUser.getName().toString());
            holder.infoPage.setBackgroundColor(getResources().getColor(R.color.orange));
            holder.nickName.setText(friend.getName());
            holder.surname.setText(friend.getSurname());

        }

        class FriendsHolder {
            TextView leftAvatar;
            TextView rightAvatar;
            View infoPage;
            List<TextView> interests = new ArrayList<>();
            TextView nickName;
            TextView surname;


        }

    }
}
