package com.example.alessandro.loginandroid.Entity;

/*
* e' la classe che ci permette di gestire le risposte del server
 */
public class Response {
    private User user;
    private String access_Token;
    private String refresh_Token;
    private String type;

    public String getAccess_Token() {
        return access_Token;
    }

    public void setAccess_Token(String access_Token) {
        this.access_Token = access_Token;
    }

    public String getRefresh_Token() {
        return refresh_Token;
    }

    public void setRefresh_Token(String refresh_Token) {
        this.refresh_Token = refresh_Token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
