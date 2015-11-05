package com.example.alessandro.loginandroid;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alessandro on 21/10/2015.
 */
/**
 * Created by tundealao on 29/03/15.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("nome", user.nome);
        userLocalDatabaseEditor.putString("cognome", user.cognome);
        userLocalDatabaseEditor.putString("sesso", user.sesso);
        userLocalDatabaseEditor.putString("data_di_nascita", user.data_di_nascita);
        userLocalDatabaseEditor.putString("ruolo", user.ruolo);
        userLocalDatabaseEditor.putString("descrizione", user.descrizione);
        userLocalDatabaseEditor.putString("nome_utente", user.nome_utente);
        userLocalDatabaseEditor.putString("password", user.password);
        userLocalDatabaseEditor.putString("email", user.email);;
        userLocalDatabaseEditor.putInt("telefono", user.telefono);
        userLocalDatabaseEditor.putInt("tariffa", user.tariffa);


        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public User getLoggedInUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return null;
        }

        String nome = userLocalDatabase.getString("nome", "");
        String cognome = userLocalDatabase.getString("cognome", "");
        String sesso = userLocalDatabase.getString("sesso", "");
        String data_di_nascita = userLocalDatabase.getString("data_di_nascita", "");
        String indirizzo = userLocalDatabase.getString("indirizzo", "");
        String ruolo = userLocalDatabase.getString("ruolo", "");
        String descrizione = userLocalDatabase.getString("descrizione", "");
        String foto_profilo = userLocalDatabase.getString("foto_profilo", "");
        String nome_utente = userLocalDatabase.getString("nome_utente", "");
        String password = userLocalDatabase.getString("password", "");
        String email = userLocalDatabase.getString("email", "");
        int telefono = userLocalDatabase.getInt("telefono", -1);
        int tariffa = userLocalDatabase.getInt("tariffa", -1);
        User user = new User(nome, cognome, sesso, data_di_nascita, indirizzo, ruolo,
                descrizione, foto_profilo, nome_utente, password, email,
                telefono, tariffa);
        return user;
    }
}
