package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alessandro.loginandroid.Adapters.ListUser;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



import static android.R.layout.simple_dropdown_item_1line;

public class testSearchActivity extends ActionBarActivity implements View.OnClickListener {

    private AutoCompleteTextView searchRoleText;
    private AutoCompleteTextView searchNameText;
    private AutoCompleteTextView searchCityText;
    private SeekBar searchRateSeekBar;
    private TextView searchRateText;
    private int pro;
    ImageButton buttonHOME, buttonSEARCH, buttonFOLLOW, buttonPROFILE;


    ArrayAdapter adapterNames, adapterRoles, adapterCites;
    ArrayList<String> namesA, rolesA, citiesA;
    String[] names;
    String[] roles;
    String[] cities;
    ArrayList<User> users;
    ClientLocalStore clientLocalStore;
    Button buttonSTART;
    ListView listViewSearched;
    ListUser listUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_search);

        searchRateText = (TextView)findViewById(R.id.searchRateText);
        searchNameText = (AutoCompleteTextView)findViewById(R.id.searchNameText);
        searchRoleText = (AutoCompleteTextView)findViewById(R.id.searchRoleText);
        searchCityText = (AutoCompleteTextView)findViewById(R.id.searchCityText);
        searchRateSeekBar = (SeekBar)findViewById(R.id.searchRateSeekBar);


        buttonHOME = (ImageButton) findViewById(R.id.buttonHOME);
        buttonSEARCH = (ImageButton) findViewById(R.id.buttonSEARCH);
        buttonFOLLOW = (ImageButton) findViewById(R.id.buttonFOLLOW);
        buttonPROFILE = (ImageButton) findViewById(R.id.buttonPROFILE);

        buttonHOME.setOnClickListener(this);
        buttonSEARCH.setOnClickListener(this);
        buttonFOLLOW.setOnClickListener(this);
        buttonPROFILE.setOnClickListener(this);


        listViewSearched = (ListView) findViewById(R.id.listViewSearched);
        searchRateSeekBar.setMax(200);
        searchRateSeekBar.setProgress(200);




        clientLocalStore = new ClientLocalStore(this);
        namesA = new ArrayList<>();
        citiesA = new ArrayList<>();
        rolesA = new ArrayList<>();

        //setta le liste
        users = new ArrayList<>();
        new UserListTask(clientLocalStore.getUser()).execute();
        //per rimuovere i valore che sono doppi

        Log.i("TOT UTENTI CREATE", "" + namesA.size());

        searchNameText.setAdapter(adapterNames);
        searchNameText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        searchRoleText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        searchCityText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


        searchRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro = progress;
                searchRateText.setText("Price under " + String.valueOf(progress) + "$ / h");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//
//        elasticDownloadView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                elasticDownloadView.startIntro();
//                User user = new User();
//                user.setName(searchNameText.getText().toString());
//                user.setRole(searchRoleText.getText().toString());
//                user.setCity(searchCityText.getText().toString());
//                user.setRate(searchRateSeekBar.getProgress());
//                users = new ArrayList<User>();
//                new FilteredUserListTask(user).execute();
//                listUser = new ListUser(getApplicationContext(), users);
//                listViewSearched.setAdapter(listUser);
//            }
//        });

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
            case (R.id.buttonHOME):
                i = new Intent(this, testMainActivity.class);
                break;
            case (R.id.buttonSEARCH):
                i = new Intent(this, testSearchActivity.class);
                break;
            case (R.id.buttonFOLLOW):
                i = new Intent(this, testFollowActivity.class);
                break;
            case (R.id.buttonPROFILE):
                i = new Intent(this, testProfileActivity.class);
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


        //TODO dichiare AUTOCOMPLETE per Citta e Prezzi e settare l Adapter, gia creati sopra, e
        //TODO onITEMlicklistener
        searchNameText.setAdapter(adapterNames);
        searchRoleText.setAdapter(adapterRoles);
        searchCityText.setAdapter(adapterCites);

    }

    public class FilteredUserListTask extends AsyncTask<Void, Void, Void> {

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
                JSONArray usersArray = new JSONArray(json);


                for (int i = 0; i < usersArray.length(); i++) {
                    User user = new Gson().fromJson(usersArray.get(i).toString(), User.class);
                    users.add(user);
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}



