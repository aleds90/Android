package com.example.alessandro.loginandroid.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toolbar;

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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * e' la classe che si occupa di gestire la home dell'applicazione dove andremo a pubblicizzare gli utenti
 */
public class MainActivity extends Activity implements View.OnClickListener{

    ClientLocalStore clientLocalStore;// db provvisorio per prendere i dati con cui si e' loggati
    Button bLogout;// bottone per effettuare il logout
    ListView userList;
    ArrayList<User> users;

    /**
     *Definisco come un activity viene creata
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        clientLocalStore = new ClientLocalStore(this);
        userList = (ListView) findViewById(R.id.listUsers);
        //creo collegamento tra le componenti la classe e activity_main.xml

        // definiamo e settiamo la lista di utenti che riempira la nostra listview
        users = new ArrayList<>();
        new UserListTask().execute();
        UserArrayAdapter adapter = new UserArrayAdapter(this, users);
        userList.setAdapter(adapter);

        bLogout = (Button)findViewById(R.id.bLogout);
        //definisco le componenti che hanno azioni specifiche se cliccate
        bLogout.setOnClickListener(this);
    }



    /**
     * sono presenti tutte le azioni che vengono fatte prima di accedere alla activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        //in particolare controlliamo se non abbiamo token veniamo indirizzati direttamente alla activity di login
        if (clientLocalStore.getClient().getRefreshToken().equals("")) {
            System.out.println("sei dentro");
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

      /*
     *Definisco le azioni che ogni ogni componente con ClickListener settato deve effettuare al suo click
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //se clicchiamo il bottone di logout questo ci riporta alla login activity cancellando tutti i dati salvati nel local store
            case R.id.bLogout:
                clientLocalStore.clearClient();
                Intent intent =  new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }


    /**
     * classe Task che in Background manda un post ad /authotization per effettuare il login tramite refresh
     */
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

    /**
     * ci fa prendere gli untenti da /users per il momento non é necessario un check dei tocken,
     * perche la classe non parte se non dopo LoginTask. Quindi LoginTask é ok con i token allora
     * poträ partire anche UserListTask
     */
    public class UserListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post

                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/users");
               // HttpGet httpGet = new HttpGet("http://10.0.2.2:4567/users");
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



    /**
     * Gestisce la risposta del Server
     *
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
     * @param menu gli passiamo sto menu che sará la nostra navigaton_bar(vedi res->menu)
     * @return ritorna sempre true
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.actionbar);
        tb.inflateMenu(R.menu.navigation_bar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    /**
     * @param item un po come View v per OnClickListerner solo che per gli items del menu
     * @return ritorna sempre true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.Home:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.Search:
                intent = new Intent(this, ResearchActivity.class);
                break;
            case R.id.Follow:
                intent = new Intent(this, FollowActivity.class);
                break;
            case R.id.Profil:
                intent = new Intent(this, MyProfileActivity.class);
                break;
        }
        startActivity(intent);
        return true;
    }
}