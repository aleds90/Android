package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.alessandro.loginandroid.Entity.Client;
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

public class RegisterActivity extends Activity implements View.OnClickListener{

    EditText etNome, etCognome, etData_di_nascita, etIndirizzo, etDescrizione;
    EditText etNome_utente, etPassword, etMail, etTelefono, etTariffa;
    Button bRegister, bEsc;
    RadioButton rbUomo, rbDonna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNome = (EditText) findViewById(R.id.etNome);
        etCognome = (EditText) findViewById(R.id.etCognome);
        etData_di_nascita = (EditText) findViewById(R.id.etData_di_nascita);
        etDescrizione = (EditText) findViewById(R.id.etDescrizione);
        etNome_utente = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etMail = (EditText) findViewById(R.id.etEMail);
        etIndirizzo = (EditText) findViewById(R.id.etIndirizzo);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        etTariffa = (EditText) findViewById(R.id.etTariffa);

        bRegister = (Button) findViewById(R.id.bRegister);
        bEsc = (Button) findViewById(R.id.bEsc);

        rbUomo = (RadioButton) findViewById(R.id.rbUomo);
        rbDonna = (RadioButton) findViewById(R.id.rbDonna);

        bRegister.setOnClickListener(this);
        bEsc.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bRegister:

                User user = new User();
                user.setBday(etData_di_nascita.getText().toString());
                user.setCity(etIndirizzo.getText().toString());
                user.setEmail(etMail.getText().toString());
                user.setName(etNome.getText().toString());
                user.setPassword(etPassword.getText().toString());
                user.setSurname(etCognome.getText().toString());
                user.setRate(Double.parseDouble(etTariffa.getText().toString()));
                user.setRole(etTelefono.getText().toString());
                new RegisterTask(user).execute();

                break;
            case R.id.bEsc:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
        }
    }

    public class RegisterTask extends AsyncTask<String, Void, Void> {

        private User user;

        public RegisterTask(User user) {
            this.user= user;
        }



        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/add");
                // Lista di 5 valori che mandiamo
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
                // Settiamo l Header perché il sito si aspetta un header Parametro di tipo Authorization

                // con l execute, mandiamo il post
                HttpResponse response = httpclient.execute(httppost);
                // prendiamo la risposta del server e lo salviamo come stringa in "json"
                HttpEntity entity = response.getEntity();

                String json = EntityUtils.toString(entity);
                System.out.println(json);
                handleResponse(json);

            } catch (Exception e) {
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
    private void handleResponse(String responseServer) {
        switch (responseServer){
            //case 4, aggiorniamo i due token e rimaniamo nella main activity.
            case "OK":
                Intent intent =  new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case "NO":
                Toast.makeText(this, "registrazione non e' andata a buon fine", Toast.LENGTH_LONG).show();
                break;

            //TODO: definiamo e creaimo altri casi.Non solo per il login, ma anche per altri post fatti.
        }
    }
}