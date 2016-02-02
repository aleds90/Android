package com.example.alessandro.loginandroid.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alessandro.loginandroid.Adapters.ListUser;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.example.alessandro.loginandroid.Tasks.ToServerTasks;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.R.layout.simple_dropdown_item_1line;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long RIPPLE_DURATION = 250;
    private AutoCompleteTextView nameFilter,roleFilter,cityFilter;
    private SeekBar rate_sb;
    private TextView rate_tv;
    private int pro;
    ImageButton home, search, relation, profile;
    ArrayAdapter adapterNames, adapterRoles, adapterCites;
    ArrayList<String> namesA, rolesA, citiesA;
    String[] names;
    String[] roles;
    String[] cities;
    ArrayList<User> users;
    ClientLocalStore clientLocalStore;
    Button startSearch;
    ListView listViewSearched;
    ListUser listUser;
    LinearLayout searchLayout,searchListViewLayout;
    ImageView hamburger;
    Toolbar toolbar;
    FrameLayout root;
    TextView logout,delete,settings,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_search);

        setTextView();
        createToolbar();

        //setta le liste

        new UserListTask(clientLocalStore.getUser()).execute();
        //per rimuovere i valore che sono doppi

        Log.i("TOT UTENTI CREATE", "" + namesA.size());

        nameFilter.setAdapter(adapterNames);
        nameFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                String selected = (String) adapter.getItemAtPosition(pos);
                Toast.makeText(
                        getApplicationContext(),
                        "hai selezionato " + selected,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
        roleFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                String selected = (String) adapter.getItemAtPosition(pos);
                Toast.makeText(
                        getApplicationContext(),
                        "hai selezionato " + selected,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
        roleFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                String selected = (String) adapter.getItemAtPosition(pos);
                Toast.makeText(
                        getApplicationContext(),
                        "hai selezionato " + selected,
                        Toast.LENGTH_LONG
                ).show();

            }
        });


        rate_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro = progress;
                rate_tv.setText("Max: " + String.valueOf(progress) + "$ / h");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startSearch.getText().equals("AVVIA RICERCA")){
                    User user = new User();
                    user.setName(nameFilter.getText().toString());
                    user.setRole(roleFilter.getText().toString());
                    user.setCity(cityFilter.getText().toString());
                    user.setRate(rate_sb.getProgress());

                    users = new ArrayList<User>();


                    searchLayout.setVisibility(View.GONE);
                    final float scale = getResources().getDisplayMetrics().density;
                    int dpHeightInPx = (int) (400 * scale);
                    searchListViewLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpHeightInPx));
                    startSearch.setText("↓ TORNA AI FILTRI ↓");
                    new FilteredUserListTask(user).execute();
                }else{
                    searchLayout.setVisibility(View.VISIBLE);
                    final float scale = getResources().getDisplayMetrics().density;
                    int dpHeightInPx = (int) (220 * scale);
                    searchListViewLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpHeightInPx));
                    startSearch.setText("AVVIA RICERCA");

                }
            }
        });

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
                                //updateStatus();

                            }
                        });


                    }

                    @Override
                    public void onGuillotineClosed() {

                    }

                })
                .build();
    }

    private void setTextView() {
        rate_tv = (TextView)findViewById(R.id.search_rate_tv);
        nameFilter = (AutoCompleteTextView)findViewById(R.id.search_namefilter_actv);
        roleFilter = (AutoCompleteTextView)findViewById(R.id.search_rolefilter_actv);
        cityFilter = (AutoCompleteTextView)findViewById(R.id.search_cityfilter_actv);
        rate_sb = (SeekBar)findViewById(R.id.search_ratefilter_sb);
        searchLayout =(LinearLayout)findViewById(R.id.searchLayout);
        searchListViewLayout = (LinearLayout)findViewById(R.id.searchListViewLayout);

        home = (ImageButton) findViewById(R.id.home);
        search = (ImageButton) findViewById(R.id.search);
        relation = (ImageButton) findViewById(R.id.relation);
        profile = (ImageButton) findViewById(R.id.profile);
        startSearch = (Button)findViewById(R.id.search_startsearch_btn);

        home.setOnClickListener(this);
        search.setOnClickListener(this);
        relation.setOnClickListener(this);
        profile.setOnClickListener(this);


        listViewSearched = (ListView) findViewById(R.id.search_resultlist_lv);
        rate_sb.setMax(200);
        rate_sb.setProgress(200);

        clientLocalStore = new ClientLocalStore(this);
        namesA = new ArrayList<>();
        citiesA = new ArrayList<>();
        rolesA = new ArrayList<>();
        users = new ArrayList<>();
    }

    private void removeDoubleFromList(List<String> lista) {
        Set<String> set = new HashSet<>();
        set.addAll(lista);
        lista.clear();
        lista.addAll(set);
    }

    private String[] convertiDaArray(List<String> l) {
        String[] s = new String[l.size()];
        s = l.toArray(s);
        return s;
    }

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

    public class UserListTask extends AsyncTask<Void, Void, Void> {
        private User user;


        public UserListTask(User user) {
            this.user = user;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("TOT POSTEXECUTE UTENTI", "" + namesA.size());
            setEveryThing();


        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post

                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/users");
                // HttpGet httpGet = new HttpGet("http://10.0.2.2:4567/users");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                JSONArray usersArray = new JSONArray(json);

                for (int i = 0; i < usersArray.length(); i++) {
                    User user = new Gson().fromJson(usersArray.get(i).toString(), User.class);
                    namesA.add(user.getName());
                    rolesA.add(user.getRole());
                    citiesA.add(user.getCity());
                    Log.i("INSERITO NUOVO UTENTE", user.getName() + " " + user.getRole() + " " +
                            user.getCity());
                    Log.i("TOT UTENTI", "" + namesA.size());
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    private void setEveryThing() {
        names = convertiDaArray(namesA);
        roles = convertiDaArray(rolesA);
        cities = convertiDaArray(citiesA);


        adapterNames = new ArrayAdapter<String>(this, simple_dropdown_item_1line, names);

        adapterRoles = new ArrayAdapter<String>
                (this, simple_dropdown_item_1line, roles);

        adapterCites = new ArrayAdapter<String>
                (this, simple_dropdown_item_1line, cities);

        nameFilter.setAdapter(adapterNames);
        roleFilter.setAdapter(adapterRoles);
        cityFilter.setAdapter(adapterCites);

    }

    private void setAdapter() {
        listUser = new ListUser(getApplicationContext(), users);
        listViewSearched.setAdapter(listUser);

    }

    public class FilteredUserListTask extends AsyncTask<Void, Void, Void> {

        JSONArray usersArray;
        private User user;

        public FilteredUserListTask(User user) {
            this.user = user;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post

                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/getFiltered");
                // HttpGet httpGet = new HttpGet("http://10.0.2.2:4567/users");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
                nameValuePairs.add(new BasicNameValuePair("city", user.getCity()));
                nameValuePairs.add(new BasicNameValuePair("role", user.getRole()));
                nameValuePairs.add(new BasicNameValuePair("rate", Double.toString(user.getRate())));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                usersArray = new JSONArray(json);





            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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



