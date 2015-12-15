package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Adapters.ListUser;
import com.example.alessandro.loginandroid.Entity.Client;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Response;
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
import java.util.List;

public class testMainActivity extends Activity implements View.OnClickListener {

    ImageButton buttonHOME, buttonSEARCH, buttonFOLLOW, buttonPROFILE;
    TextView textviewHOME;
    ListView listViewUSERLIST;
    ClientLocalStore clientLocalStore;
    ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        clientLocalStore = new ClientLocalStore(this);

        textviewHOME = (TextView) findViewById(R.id.textViewHOME);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/font.ttf");
        textviewHOME.setTypeface(typeface);


        buttonHOME = (ImageButton) findViewById(R.id.buttonHOME);
        buttonSEARCH = (ImageButton) findViewById(R.id.buttonSEARCH);
        buttonFOLLOW = (ImageButton) findViewById(R.id.buttonFOLLOW);
        buttonPROFILE = (ImageButton) findViewById(R.id.buttonPROFILE);

        buttonHOME.setOnClickListener(this);
        buttonSEARCH.setOnClickListener(this);
        buttonFOLLOW.setOnClickListener(this);
        buttonPROFILE.setOnClickListener(this);

        listViewUSERLIST = (ListView) findViewById(R.id.listViewUSERLIST);

        users = new ArrayList<>();
        new UserListTask(clientLocalStore.getUser()).execute();
        ListUser adapter = new ListUser(this, users);

        listViewUSERLIST.setAdapter(adapter);

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
            new LoginTask(client).execute();
        }
    }

    public class LoginTask extends AsyncTask<String, Void, Void> {

        // attributi necessari per il login  tramite refresh
        private Client client;

        //costruttore
        public LoginTask(Client client) {
            this.client = client;
        }

        /*
         * Lavoro che svolge in background la classe Task. Invia la richiesta ad un indirizzo e riceve una risposta da quest'ultimo
         */
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/authorization");
                // Lista dei valori che mandiamo
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("random_id", client.getRANDOM_ID()));
                nameValuePairs.add(new BasicNameValuePair("secret_id", client.getSECRET_ID()));
                nameValuePairs.add(new BasicNameValuePair("grant_types", client.getGRANT_TYPES()));
                //Mettere la lista nel post, cosi da poterla mandare
                // prima l aveva solo creata ma non settata come da mandare
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Settiamo l Header perché il sito si aspetta un header Parametro di tipo Authorization
                httppost.setHeader("Authorization", client.getRefreshToken());
                // con l execute, mandiamo il post
                HttpResponse response = httpclient.execute(httppost);
                // prendiamo la risposta del server e lo salviamo come stringa in "json"
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println(json);
                // Creiamo una classe di tipo Response grazie alla risposta json
                Response responseServer = new Gson().fromJson(json, Response.class);
                // Metodo per gestire la risposta ottenuta
                handleResponse(responseServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class UserListTask extends AsyncTask<Void, Void, Void> {
        private User user;
        private JSONArray usersArray;

        public UserListTask(User user) {
            this.user = user;
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
                usersArray = new JSONArray(json);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void handleResponse(Response responseServer) {
        switch (responseServer.getType()) {
            //case 4, login tramite refresh andato a buon fine. otteniamo due token nuovi che salviamo nel local store
            case "4":
                User user = clientLocalStore.getUser();
                System.out.println(user.getName());
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



}
