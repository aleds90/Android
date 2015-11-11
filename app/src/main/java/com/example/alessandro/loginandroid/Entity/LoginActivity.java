package com.example.alessandro.loginandroid.Entity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alessandro.loginandroid.Activity.MainActivity;
import com.example.alessandro.loginandroid.Entity.Client;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

public class LoginActivity extends Activity implements View.OnClickListener {

    EditText etEmail, etPassword;
    Button bLogin;
    TextView tvRegister;
    ClientLocalStore clientLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        bLogin = (Button)findViewById(R.id.bLogin);
        tvRegister = (TextView)findViewById(R.id.tvRegister);

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
                Client client = new Client(user, "niente", "niente", "Password");
                client.login();// dopo questa funzione i dati del Client sono stati attualizzati
                // se i Token sono stati attuali sono stati attualizzati vuol dire che il Login é stato valido
                if(client.accessToken.length() == 26){
                    // Attualizziamo il Client dello sharedPreference
                    clientLocalStore.storeClientData(client);
                    //torniamo su Home?! Main
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    // GLi dicamo che i dati sono errati?
                    Toast.makeText(this, "Dati errati riprova",Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.tvRegister:
                //TODO: start Register Activity
                break;
        }
    }
}
