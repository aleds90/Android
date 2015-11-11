package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.LoginActivity;
import com.example.alessandro.loginandroid.R;


public class MainActivity extends Activity {

    ClientLocalStore clientLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientLocalStore = new ClientLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(clientLocalStore.getClient().isActive()){
            //TODO: crea la Home, ovvero cambia/setta i componenti del Layout con i dati dell Utente
        }else{
            // vai al login visto che probabilmente i token sono scaduti
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}