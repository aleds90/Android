package com.example.alessandro.loginandroid.Entity;


/**
 * Classe che implementa l Utente compresi i dati dei Token
 */
public class Client {

    String accessToken, refreshToken, RANDOM_ID, SECRET_ID, GRANT_TYPES; // Dati del Client + Token

    /**
     * Costruttore, i RANDOM_ID e SECRET_ID non servono in quanto sono costanti
     * @param accessToken
     * @param refreshToken
     * @param GRANT_TYPES
     */
    public Client(String accessToken, String refreshToken, String GRANT_TYPES) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.RANDOM_ID = "ciao";
        this.SECRET_ID = "ciao";
        this.GRANT_TYPES = GRANT_TYPES;
    }

    /**
     * Costruttore vuoto
     */
    public Client(){}



    /**
     * Vede se i token sono attivi o meno, quindi ci dice che i dati dell utente sono ancora validi.
     * @return
     */
    public boolean isActive() {
        //TODO chiedi al server il client é attivo, ovvero vedi sei i token sono OK
        return false;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getGRANT_TYPES() {
        return GRANT_TYPES;
    }

    public void setGRANT_TYPES(String GRANT_TYPES) {
        this.GRANT_TYPES = GRANT_TYPES;
    }

    public String getRANDOM_ID() {
        return RANDOM_ID;
    }

    public void setRANDOM_ID(String RANDOM_ID) {
        this.RANDOM_ID = RANDOM_ID;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getSECRET_ID() {
        return SECRET_ID;
    }

    public void setSECRET_ID(String SECRET_ID) {
        this.SECRET_ID = SECRET_ID;
    }
}