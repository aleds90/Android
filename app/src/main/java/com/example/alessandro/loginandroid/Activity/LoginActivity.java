package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

/**
 * e' la classe che si occupa di gestire l'interfaccia per effettuare un login.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    EditText etEmail, etPassword; // attributi per inserire l'email e la password
    Button bLogin; // bottone che effettua il login
    TextView tvRegister; //bottone che manda all'interfaccia di registrazione
    ClientLocalStore clientLocalStore; //db provvisorio per raccogliere i dati della session


    /**
     *Definisco come un activity viene creata
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        clientLocalStore = new ClientLocalStore(this);
        //creo collegamento tra le componenti la classe e activity_login.xml
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        bLogin = (Button)findViewById(R.id.bLogin);
        tvRegister = (TextView)findViewById(R.id.tvRegister);
        //definisco le componenti che hanno azioni specifiche se cliccate
        bLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    /*
     *Definisco le azioni che ogni ogni componente con ClickListener settato deve effettuare al suo click
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //il bLogin richiama il metodo login con email e password inseriti nelle componenti e creando un client predefinito
            case R.id.bLogin:
                User user = new User();
                user.setEmail(etEmail.getText().toString());
                user.setPassword(etPassword.getText().toString());
                Client client = new Client("ciao", "ciao", "Password");
                login(client, user);
                break;
            //il tvRegister invia l'utente all'interfaccia per la registrazione
            case R.id.tvRegister:
                Intent intent =  new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Metodo per richiamare la classe login nella classe principale al server i dati del login e verificare se sono corretti o meno
     */
    public void login(Client client, User user){
        new LoginTask(client, user).execute();
    }
    /**
     * classe Task che in Background manda un post ad /authotization
     */
    public class LoginTask extends AsyncTask<String, Void, Void> {

        //attributi necessari per provare ad effettuare un login
        private Client client;
        private User user;

        //costruttore della classe Task
        public LoginTask(Client client, User user){
            this.client=client;
            this.user=user;
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
                List<NameValuePair> nameValuePairs = new ArrayList<>(5);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("random_id", client.getRANDOM_ID()));
                nameValuePairs.add(new BasicNameValuePair("secret_id", client.getSECRET_ID()));
                nameValuePairs.add(new BasicNameValuePair("grant_types", client.getGRANT_TYPES()));
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
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
                Response responseServer = new Gson().fromJson(json,Response.class);
                // Metodo per gestire la risposta ottenuta
                handleResponse(responseServer);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Gestisce la risposta del Server e indentifica ogni azione da svolgere per ogni tipo di risposta che si puo' ottenere
     *
     */
    private void handleResponse(Response responseServer) {
        switch (responseServer.getType()){
            // response:2, login effettuato. Inseriamo i Token ottenuti, i dati dell'utente e del client nel nostro db e veniamo inviati nella main activity
            case "2":
                User user = responseServer.getUser();
                Client client = new Client(responseServer.getAccess_Token(),responseServer.getRefresh_Token(), "");
                clientLocalStore.storeClientData(client,user);
                Intent intent = new Intent(this, testMainActivity.class);
                startActivity(intent);
                break;
            //response:3, il login non e' andato a buon fine quindi rimane sulla login activity per ritentare
            case "3":
                break;
        }
    }
}
