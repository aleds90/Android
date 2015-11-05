package com.example.alessandro.loginandroid;


public class User {

    String nome,cognome, sesso, data_di_nascita, indirizzo, ruolo, descrizione, foto_profilo, nome_utente, password, email;

    int telefono, tariffa;

    public User(String nome, String cognome, String sesso, String data_di_nascita, String indirizzo, String ruolo, String descrizione, String foto_profilo, String nome_utente, String password, String email, int telefono, int tariffa) {
        this.nome = nome;
        this.cognome = cognome;
        this.sesso = sesso;
        this.data_di_nascita = data_di_nascita;
        this.indirizzo = indirizzo;
        this.ruolo = ruolo;
        this.descrizione = descrizione;
        this.foto_profilo = foto_profilo;
        this.nome_utente = nome_utente;
        this.password = password;
        this.email = email;
        this.telefono = telefono;
        this.tariffa = tariffa;
    }

     public User(String username, String password) {
        this("", "", "", "", "", "", "", "", username, password, "", -1, -1);
    }
}