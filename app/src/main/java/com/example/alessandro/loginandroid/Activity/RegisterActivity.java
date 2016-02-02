package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.example.alessandro.loginandroid.Tasks.ToServerTasks;

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
 * Classe che si occupa di gestire l'interfaccia per la registrazione
 */
public class RegisterActivity extends Activity implements View.OnClickListener{
    EditText name, surname, email, password, bday,city,rate;
    Button register, menu;
    Spinner roles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_register);
        setViews();

    }

    /**
     * Metodo che inizializza tutti le componenti della Register Activity
     */
    private void setViews() {
        name        = (EditText) findViewById(R.id.register_name);
        surname     = (EditText) findViewById(R.id.register_surname);
        email       = (EditText) findViewById(R.id.register_email);
        password    = (EditText) findViewById(R.id.register_password);
        bday        = (EditText) findViewById(R.id.register_bday);
        roles       = (Spinner)findViewById(R.id.register_roles);
        city        = (EditText) findViewById(R.id.register_city);
        rate        = (EditText) findViewById(R.id.register_rate);
        register    = (Button) findViewById(R.id.register_register);
        menu        = (Button) findViewById(R.id.register_menu);

        register.setOnClickListener(this);
        menu.setOnClickListener(this);
    }

   /**
    *Definisco le azioni che ogni ogni componente con ClickListener settato deve effettuare al suo click
    */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_register:
                User user = new User();
                user.setName(name.getText().toString());
                user.setSurname(surname.getText().toString());
                user.setEmail(email.getText().toString());
                user.setPassword(password.getText().toString());
                user.setBday(bday.getText().toString());
                user.setRole(roles.getSelectedItem().toString());
                user.setCity(city.getText().toString());
                user.setRate(Double.parseDouble(rate.getText().toString()));
                user.setAvatar(1);
                new RegisterTask(user).execute();
                break;

            case R.id.register_menu:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * classe Task che in Background manda un post ad /add per effettuare la registrazione sul server
     */
    public class RegisterTask extends AsyncTask<String, Void, Void> {
        private User user;

        public RegisterTask(User user) {
            this.user= user;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://njsao.pythonanywhere.com/insert_user");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(9);
                nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
                nameValuePairs.add(new BasicNameValuePair("surname", user.getSurname()));
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("bday", user.getBday()));
                nameValuePairs.add(new BasicNameValuePair("role", user.getRole()));
                nameValuePairs.add(new BasicNameValuePair("city", user.getCity()));
                nameValuePairs.add(new BasicNameValuePair("rate", Double.toString(user.getRate())));
                nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
                nameValuePairs.add(new BasicNameValuePair("avatar", Integer.toString(user.getAvatar())));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
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
            default:
                Toast.makeText(RegisterActivity.this, "Register Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}