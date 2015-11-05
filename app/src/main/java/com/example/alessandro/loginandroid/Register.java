package com.example.alessandro.loginandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


public class Register extends Activity implements View.OnClickListener{
    EditText etNome, etCognome, etData_di_nascita, etIndirizzo, etDescrizione;
    EditText etNome_utente, etPassword, etMail, etTelefono, etTariffa;
    Button bRegister, bEsci;
    RadioButton rbUomo, rbDonna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpage1);

        etNome = (EditText)findViewById(R.id.etNome);
        etCognome = (EditText)findViewById(R.id.etCognome);
        etData_di_nascita = (EditText)findViewById(R.id.etData_di_nascita);
        etDescrizione = (EditText)findViewById(R.id.etDescrizione);
        etNome_utente = (EditText)findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etMail = (EditText)findViewById(R.id.etEMail);
        etIndirizzo = (EditText)findViewById(R.id.etIndirizzo);
        etTelefono = (EditText)findViewById(R.id.etTelefono);
        etTariffa = (EditText)findViewById(R.id.etTariffa);

        bRegister = (Button) findViewById(R.id.bRegistra);
        bEsci = (Button)findViewById(R.id.bEsci);

        rbUomo = (RadioButton)findViewById(R.id.rbUomo);
        rbDonna = (RadioButton)findViewById(R.id.rbDonna);

        bRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRegistra:
                String nome = etNome.getText().toString();
                String cognome = etCognome.getText().toString();
                String data_di_nascita = etData_di_nascita.getText().toString();
                String indirizzo = etIndirizzo.getText().toString();
                String ruolo = "allenatore";
                String descrizione = etDescrizione.getText().toString();
                String foto_profilo = "perOraNulla";
                String nome_utente = etNome_utente.getText().toString();
                String password = etPassword.getText().toString();
                String mail = etMail.getText().toString();
                int telefono = Integer.getInteger(etTelefono.getText().toString(), 0);
                int tariffa = Integer.getInteger(etTariffa.getText().toString(),0);
                String sesso = getSesso();

                User user = new User(nome, cognome, sesso, data_di_nascita, indirizzo, ruolo,
                        descrizione, foto_profilo, nome_utente, password, mail, telefono, tariffa);
                registerUser(user);
                break;
        }
    }

    private String getSesso() {
        if(rbUomo.isChecked()){
            return "uomo";
        }
        else{
            return "donna";
        }
    }

    private void registerUser(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                Intent loginIntent = new Intent(Register.this, Login.class);
                startActivity(loginIntent);
            }
        });
    }
}
