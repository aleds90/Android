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
import com.example.alessandro.loginandroid.Tasks.ToServerTasks;
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
 * classe che si occupa di gestire l'interfaccia per effettuare un login.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    EditText etEmail, etPassword; // attributi per inserire l'email e la password
    Button bLogin; // bottone che effettua il login
    TextView tvRegister; //bottone che manda all'interfaccia di registrazione
    ClientLocalStore clientLocalStore; //db provvisorio per raccogliere i dati della session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        clientLocalStore = new ClientLocalStore(this);
        createView();

    }

    /**
     * Metodo per settare tutte le view presenti nella Login Activity
     */
    private void createView() {
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
                Client client = new Client("Ciao", "Ciao", "Password");
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
        new ToServerTasks.LoginTask(client, user, clientLocalStore,this).execute();
    }
}
