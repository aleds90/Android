package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alessandro.loginandroid.Activity.MainActivity;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener {

    EditText etEmail, etPassword;
    Button bLogin;
    TextView tvRegister;
    ClientLocalStore clientLocalStore;
    Button bError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        bLogin = (Button)findViewById(R.id.bLogin);
        tvRegister = (TextView)findViewById(R.id.tvRegister);
        bError = (Button)findViewById(R.id.bError);

        bLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        clientLocalStore = new ClientLocalStore(this);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bLogin:
                User user = new User();
                user.setEmail(etEmail.getText().toString());
                user.setPassword(etPassword.getText().toString());
                // i token nel caso di un richiesta di login non ci interessano e li usiamo per vedere se il
                // login é stato effettuato.(se cambiano -> effettuato)
                Client client = new Client("ciao", "ciao", "Password");
                login(client, user);// dopo questa funzione i dati del Client sono stati attualizzati
                // se i Token sono stati attuali sono stati attualizzati vuol dire che il Login é stato valido
//                if(client.accessToken.length() == 26){
//                    // Attualizziamo il Client dello sharedPreference
//                    clientLocalStore.storeClientData(client);
//                    //torniamo su Home?! Main
//                    Intent intent = new Intent(this, MainActivity.class);
//                    startActivity(intent);
//                }
//                else{
//                    // GLi dicamo che i dati sono errati?
//                    Toast.makeText(this, "Dati errati riprova",Toast.LENGTH_LONG).show();
//                }

                break;
            case R.id.tvRegister:
                //TODO: start Register Activity
                break;
        }
    }

    /**
     * Metodo per il Login/ovvero sparare a /authorization
     */
    public void login(Client client, User user){
        new LoginTask(client, user).execute();
    }

    /**
     * Task che in Background manda un post ad /authotization
     */
    public class LoginTask extends AsyncTask<String, Void, Void> {

        private Client client;
        private User user;

        public LoginTask(Client client, User user){

            this.client=client;
            this.user=user;
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
     * Gestisce la risposta del Server
     * @param responseServer
     */
    private void handleResponse(Response responseServer) {
        switch (responseServer.getType()){
            // -2- login effettuato e attualizzati entrambi i Token e ovviamente aggiungiamo tutti i dati del Utente
            case "2":
                User user = responseServer.getUser();
                Client client = new Client(responseServer.getAccess_Token(),responseServer.getRefresh_Token(), "");
                clientLocalStore.storeClientData(client,user);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case "3":
                bError.setVisibility(View.VISIBLE);
                break;

            //TODO: definiamo e creaimo altri casi.Non solo per il login, ma anche per altri post fatti.
        }
    }
}
