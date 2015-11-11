package com.example.alessandro.loginandroid.Entity;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.alessandro.loginandroid.Activity.MainActivity;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Classe che implementa l Utente compresi i dati dei Token
 */
public class Client {
    User currentUser; // dati dell utente
    String accessToken, refreshToken, RANDOM_ID, SECRET_ID, GRANT_TYPES; // Dati del Client + Token

    /**
     * Costruttore, i RANDOM_ID e SECRET_ID non servono in quanto sono costanti
     * @param currentUser
     * @param accessToken
     * @param refreshToken
     * @param GRANT_TYPES
     */
    public Client(User currentUser, String accessToken, String refreshToken, String GRANT_TYPES) {
        this.currentUser = currentUser;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.RANDOM_ID = "ciao";
        this.SECRET_ID = "ciao";
        this.GRANT_TYPES = GRANT_TYPES;
    }

    /**
     * Costruttore vuoto
     */
    public Client(){}

    /**
     * Metodo per il Login/ovvero sparare a /authorization
     */
    public void login(){
        new LoginTask(this).execute();
    }

    /**
     * Vede se i token sono attivi o meno, quindi ci dice che i dati dell utente sono ancora validi.
     * @return
     */
    public boolean isActive() {
        //TODO chiedi al server il client é attivo, ovvero vedi sei i token sono OK
        return false;
    }


    /**
     * Task che in Background manda un post ad /authotization
     */
    public class LoginTask extends AsyncTask<String, Void, Void>{

        private Client client;

        public LoginTask(Client client){

            this.client=client;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/authorization");
                // Lista di 5 valori che mandiamo
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("random_id", client.RANDOM_ID));
                nameValuePairs.add(new BasicNameValuePair("secret_id", client.SECRET_ID));
                nameValuePairs.add(new BasicNameValuePair("grant_types", client.GRANT_TYPES));
                nameValuePairs.add(new BasicNameValuePair("email", client.currentUser.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("password", client.currentUser.getEmail()));
                //Mettere la lista nel post, cosi da poterla mandare
                // prima l aveva solo creata ma non settata come da mandare
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Settiamo l Header perché il sito si aspetta un header Parametro di tipo Authorization
                httppost.setHeader("Authorization", client.refreshToken);

                // con l execute, mandiamo il post
                HttpResponse response = httpclient.execute(httppost);
                // prendiamo la risposta del server e lo salviamo come stringa in "json"
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                // Creiamo una classe di tipo Response grazie alla risposta json
                Response responseServer = new Gson().fromJson(json, Response.class);
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
     * Gestisce la risposta del Server
     * @param responseServer
     */
    private void handleResponse(Response responseServer) {
        switch (responseServer.getType()){
            // -2- login effettuato e attualizzati entrambi i Token e ovviamente aggiungiamo tutti i dati del Utente
            case "2":
                this.currentUser = responseServer.getUser();
                this.accessToken = responseServer.getAccess_Token();
                this.refreshToken = responseServer.getRefresh_Token();
                break;
            //TODO: definiamo e creaimo altri casi.Non solo per il login, ma anche per altri post fatti.
        }
    }
}