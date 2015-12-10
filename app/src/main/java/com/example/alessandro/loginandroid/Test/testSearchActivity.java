package com.example.alessandro.loginandroid.Test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

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

public class testSearchActivity extends Activity {

    private AutoCompleteTextView searchRoleAutoComplete;
    List<String> roles;
    List<String> cities;
    String[] prices = {"0", "10", "15", "20", "25", "50", "75", "100"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_search);

        searchRoleAutoComplete = (AutoCompleteTextView)findViewById(R.id.searchRoleAutoComplete);

        //setta le liste
        new UserListTask(new ClientLocalStore(this).getUser()).execute();


        ArrayAdapter<String> adapterRoles = new ArrayAdapter<String>
                (this,android.R.layout.simple_dropdown_item_1line, roles);

        ArrayAdapter<String> adapterCites = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, cities);

        ArrayAdapter<String> adapterPrices = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, prices);

        //TODO dichiare AUTOCOMPLETE per Citta e Prezzi e settare l Adapter, gia creati sopra, e
        //TODO onITEMlicklistener

        searchRoleAutoComplete.setAdapter(adapterRoles);
        searchRoleAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    }


    public class UserListTask extends AsyncTask<Void, Void, Void> {
        private User user;

        public UserListTask(User user) {
            this.user = user;
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
                roles = new ArrayList<String>();
                cities = new ArrayList<String>();
                for (int i = 0; i < usersArray.length(); i++) {
                    User user = new Gson().fromJson(usersArray.get(i).toString(), User.class);
                    roles.add(user.getRole());
                    cities.add(user.getCity());
                }
                //per rimuovere i valore che sono doppi
                removeDoubleFromList(roles);
                removeDoubleFromList(cities);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void removeDoubleFromList(List<String> lista) {
            Set<String> set = new HashSet<>();
            set.addAll(lista);
            lista.clear();
            lista.addAll(set);
        }
    }

}
