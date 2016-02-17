package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Entity.Client;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.example.alessandro.loginandroid.Tasks.ToServerTasks;

/**
 * classe che si occupa di gestire l'interfaccia per effettuare un login.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    EditText etEmail, etPassword; // attributi per inserire l'email e la password
    Button bLogin; // bottone che effettua il login
    TextView tvRegister, register_visitor; //bottone che manda all'interfaccia di registrazione
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
        register_visitor = (TextView)findViewById(R.id.login_registerVisitor_tv);
        //definisco le componenti che hanno azioni specifiche se cliccate
        bLogin.setOnClickListener(this);
        register_visitor.setOnClickListener(this);
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
                Intent intent =  new Intent(this, RegisterWorkerActivity.class);
                startActivity(intent);
                break;
            case R.id.login_registerVisitor_tv:
                Intent intent1 =  new Intent(this, RegisterVisitorActivity.class);
                startActivity(intent1);
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
