package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

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
 * e' la classe che si occupa di gestire l'interfaccia per la registrazione
 */
public class RegisterActivity extends Activity implements View.OnClickListener{
    //field dove vengono inseriti i campi per la registrazione
    EditText editTextName, editTextSurname, editTextEmail, editTextPassword, editTextBirthday,editTextRole,editTextCity,editTextRate;
    //bottoni presenti nella activity di registrazione
    Button bRegister, bBackToMenu;
    Spinner roles;
    /**
     *Definisco come un activity viene creata
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // collego tutte le componenti ai corrispondenti elementi del activity_register.xml
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextSurname = (EditText) findViewById(R.id.editTextSurname);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextBirthday = (EditText) findViewById(R.id.editTextBirthday);
        roles = (Spinner)findViewById(R.id.spinnerRoles);
        editTextCity = (EditText) findViewById(R.id.editTextCity);
        editTextRate = (EditText) findViewById(R.id.editTextRate);
        // definisco le componenti che possono essere cliccate
        bRegister = (Button) findViewById(R.id.bRegisterUser);
        bBackToMenu = (Button) findViewById(R.id.bRegisterToLogin);
        bRegister.setOnClickListener(this);
        bBackToMenu.setOnClickListener(this);
    }
      /*
     *Definisco le azioni che ogni ogni componente con ClickListener settato deve effettuare al suo click
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // il bottone register invia tutti i dati inseriti al server tramite la classe Task
            case R.id.bRegisterUser:
                //creo un utente composto dai dati inseriti
                User user = new User();
                user.setName(editTextName.getText().toString());
                user.setSurname(editTextSurname.getText().toString());
                user.setEmail(editTextEmail.getText().toString());
                user.setPassword(editTextPassword.getText().toString());
                user.setBday(editTextBirthday.getText().toString());
                user.setRole(roles.getSelectedItem().toString());
                user.setCity(editTextCity.getText().toString());
                user.setRate(Double.parseDouble(editTextRate.getText().toString()));
                // richiamo la classe e invio
                new RegisterTask(user).execute();
                break;
            // serve per tornare nel menu di login nel caso si vuole annullare la registrazione
            case R.id.bRegisterToLogin:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
        }
    }

    /**
     * classe Task che in Background manda un post ad /add per effettuare la registrazione sul server
     */

    public class RegisterTask extends AsyncTask<String, Void, Void> {
        // attributo che identifica lo user che stiamo per registrare
        private User user;
        // costruttore della classe
        public RegisterTask(User user) {
            this.user= user;
        }
        /*
        * Lavoro che svolge in background la classe Task. Invia la richiesta ad un indirizzo e riceve una risposta da quest'ultimo
        */
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/add");
                // Lista dei valori che mandiamo
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
                nameValuePairs.add(new BasicNameValuePair("surname", user.getSurname()));
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("bday", user.getBday()));
                nameValuePairs.add(new BasicNameValuePair("role", user.getRole()));
                nameValuePairs.add(new BasicNameValuePair("city", user.getCity()));
                nameValuePairs.add(new BasicNameValuePair("rate", Double.toString(user.getRate())));
                nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
                //Mettere la lista nel post, cosi da poterla mandare
                // prima l aveva solo creata ma non settata come da mandare
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // con l execute, mandiamo il post
                HttpResponse response = httpclient.execute(httppost);
                // prendiamo la risposta del server e lo salviamo come stringa in "json"
                HttpEntity entity = response.getEntity();
                // la risposta in questo caso sara' una stringa che dice al client se la registrazione e' stata accettata o meno
                String json = EntityUtils.toString(entity);
                // Metodo per gestire la risposta ottenuta
                handleResponse(json);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    /**
     * Gestisce la risposta del Server
     * @param responseServer
     */
    private void handleResponse(String responseServer) {
        switch (responseServer){
            //case OK, il server ci dice che la registrazione e' stata correttamente eseguita e ci rimanda alla schermata di login
            case "OK":
                Intent intent =  new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            //case NO, indica il caso in cui la registrazione non e' andata a buon fine per probabile errori dell'utente
            case "NO":
                //TODO: creare un Popup di errore
                break;
        }
    }
}