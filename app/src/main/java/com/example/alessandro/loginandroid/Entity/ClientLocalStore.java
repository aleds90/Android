package com.example.alessandro.loginandroid.Entity;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sjnao on 11/10/15.
 */
public class ClientLocalStore {
    //Share Preference, in poche parole sono un mini DB che salva delle coppie di valori, fissi sul
    // cellulare. Quindi rimangono fisse anche quando chiudo e riapro l app, o il spegno e riaccendo
    // il dispositivo. Le usiamo per salvare i dati del client!
    public static final String SP_NAME = "ClientDetails";

    SharedPreferences clientLocalDB;

    public ClientLocalStore(Context context) {
        clientLocalDB = context.getSharedPreferences(SP_NAME, 0);
    }

    /**
     * Salviamo i dati del client in queste shared Preference
     * @param client
     */
    public void storeClientData(Client client){

        SharedPreferences.Editor editor = clientLocalDB.edit();
        editor.putString("access_Token", client.accessToken);
        editor.putString("refresh_Token", client.refreshToken);

        editor.putString("email", client.currentUser.getEmail());
        editor.putString("password", client.currentUser.getPassword());
        editor.putString("name", client.currentUser.getName());
        editor.putString("surname", client.currentUser.getSurname());
        editor.putString("city", client.currentUser.getCity());
        editor.putString("role", client.currentUser.getRole());
        editor.putString("bday", client.currentUser.getBday().toString());
        editor.putFloat("rate", (float) client.currentUser.getRate());

        editor.commit();

    }

    /**
     * @return il client salvato in precendza
     */
    public Client getClient(){

        String access_token = clientLocalDB.getString("access_Token", "");
        String refresh_token = clientLocalDB.getString("refresh_Token", "");

        String email = clientLocalDB.getString("email", "");
        String password = clientLocalDB.getString("password", "");
        String name = clientLocalDB.getString("name", "");
        String surname = clientLocalDB.getString("surname", "");
        String city = clientLocalDB.getString("city", "");
        String role = clientLocalDB.getString("role", "");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date bday = null;
        try {
             bday = dateFormat.parse(clientLocalDB.getString("bday", ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double rate = (double)clientLocalDB.getFloat("password", 0f);

        User user = new User(name,surname,email,password,bday,role,city,rate);
        Client client = new Client(user,access_token, refresh_token, "");

        return client;
    }

    /**
     * Cancella tutti dati salvati, in caso di un log out(?) o di un reset.
     */
    public void clearClient(){
        SharedPreferences.Editor editor = clientLocalDB.edit();
        editor.clear();
        editor.commit();
    }

}
