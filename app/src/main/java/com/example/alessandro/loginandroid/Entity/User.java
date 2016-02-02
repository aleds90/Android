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

    public int getDrawableAvatar(int avatar){
        if (avatar ==1)
            return R.drawable.anastasia;
        else if (avatar==2)
            return R.drawable.daria;
        else if (avatar==3)
            return R.drawable.julia;
        else if (avatar==4)
            return R.drawable.irene;
        else if (avatar==5)
            return R.drawable.kate;
        else if (avatar==6)
            return R.drawable.kirill;
        else if (avatar==7)
            return R.drawable.paul;
        else if (avatar==8)
            return R.drawable.alessandro;
        else
            return R.drawable.yalantis;

    }


}