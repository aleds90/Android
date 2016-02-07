package com.example.alessandro.loginandroid.Entity;


import com.example.alessandro.loginandroid.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
e' la classe che ci permette di identificare lo user
 */
public class User implements Serializable{

    private int id_user;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String bday;
    private String role;
    private String city;
    private double rate;
    private boolean active;
    private String description;
    private List<String> interests = new ArrayList<>();
    private int avatar;

    public User(int id_user, String name, String surname, String email, String password,
                String bday, String role, String city, double rate, boolean active,
                String description,int avatar) {
        this.id_user = id_user;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.bday = bday;
        this.role = role;
        this.city = city;
        this.rate = rate;
        this.active = active;
        this.description = description;
        this.avatar = avatar;
    }

    public User(){}

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getInterests() {
        interests.add(role);
        interests.add(Double.toString(rate)+"€");
        String status = new String();
        if (isActive())
            status = "Disponibile";
        else status = "Non disponibile";
        interests.add(city);
        interests.add(status);
        return interests;
    }

    public int getDrawableAvatar(String role){
        if (role.equals("Animatore"))
            return R.drawable.animatore;
        else if (role.equals("Barista")||role.equals("Barman")||role.equals("Cameriere"))
            return R.drawable.cameriere;
        else if (role.equals("Barbiere")||role.equals("Estetista")||role.equals("Parrucchiere"))
            return R.drawable.barbiere;
        else if (role.equals("Baby sitter"))
            return R.drawable.donnapercasa;
        else if (role.equals("Conducente")||role.equals("Tassista"))
            return R.drawable.autista;
        else if (role.equals("Cuoco")||role.equals("Pasticciere"))
            return R.drawable.cuoco;
        else if (role.equals("Designer")||role.equals("Grafico pubblicitario"))
            return R.drawable.artista;
        else if (role.equals("Dietista")||role.equals("Fisioterapista")||role.equals("Infermiere")
                ||role.equals("Nutrizionista")||role.equals("Nutrizionista animale")
                ||role.equals("Veterinario"))
            return R.drawable.medico;
        else if (role.equals("Elettricista")||role.equals("Idraulico")||role.equals("Muratore"))
            return R.drawable.aggiustatore;
        else if (role.equals("Fotografo"))
            return R.drawable.fotografo;
        else if (role.equals("Guardia del corpo"))
            return R.drawable.agente;
        else if (role.equals("Guida"))
            return R.drawable.guida;
        else if (role.equals("Giardiniere"))
            return R.drawable.falegname;
        else if (role.equals("Maestro di sci"))
            return R.drawable.sci;
        else if (role.equals("Preparatore sportivo")||role.equals("Procuratore sportivo")||role.equals("Personal Trainer"))
            return R.drawable.sport;
        else if (role.equals("Programmatore"))
            return R.drawable.nerd;
        else if (role.equals("Tutor per ripetizioni"))
            return R.drawable.prof;
        else
            return R.drawable.yalantis;

    }


}