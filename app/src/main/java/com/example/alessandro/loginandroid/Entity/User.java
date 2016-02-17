package com.example.alessandro.loginandroid.Entity;


import android.content.ContentValues;
import android.content.Context;
import android.widget.ImageView;

import com.example.alessandro.loginandroid.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.net.URL;
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
        String status;
        if (isActive())
            status = "Disponibile";
        else status = "Non disponibile";
        interests.add(city);
        interests.add(status);
        return interests;
    }

    public void getDrawableAvatar(String role,int avatar,ImageView imageView,Context context,String url) {
        if (avatar == 0) {
            if (role.equals("Animatore"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.animatore));
            else if (role.equals("Barista") || role.equals("Barman") || role.equals("Cameriere"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cameriere));
            else if (role.equals("Barbiere") || role.equals("Estetista") || role.equals("Parrucchiere")
                    || role.equals("HairStyler") || role.equals("Make Up Artist") || role.equals("Sarto"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.barbiere));
            else if (role.equals("Baby sitter"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cameriera));
            else if (role.equals("Conducente") || role.equals("Tassista"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.autista));
            else if (role.equals("Cuoco") || role.equals("Pasticciere"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cuoco));
            else if (role.equals("Wedding Planner"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.planner));
            else if (role.equals("Designer") || role.equals("Grafico pubblicitario") ||
                    role.equals("Pittore"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.artista));
            else if (role.equals("Dietista") || role.equals("Fisioterapista") || role.equals("Infermiere")
                    || role.equals("Nutrizionista") || role.equals("Nutrizionista animale")
                    || role.equals("Veterinario"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.medico));
            else if (role.equals("Elettricista") || role.equals("Idraulico") || role.equals("Muratore"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.aggiustatore));
            else if (role.equals("Fotografo") || role.equals("Video-Maker") || role.equals("Social-Media Manager"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.fotografo));
            else if (role.equals("Guardia del corpo"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.agente));
            else if (role.equals("Guida Turistica") || role.equals("Guida"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.guida));
            else if (role.equals("Giardiniere"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.falegname));
            else if (role.equals("Maestro di sci"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.sci));
            else if (role.equals("Fioraio"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.fioraio));
            else if (role.equals("Modello"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.modello));
            else if (role.equals("Preparatore sportivo") || role.equals("Procuratore sportivo") || role.equals("Personal Trainer"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.sport));
            else if (role.equals("Programmatore"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.nerd));
            else if (role.equals("Tutor per ripetizioni"))
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.prof));
            else
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.yalantis));
        }else if (avatar==1){
            Picasso.with(context).load(url).resize(150,150).into(imageView);
        }
        else
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.yalantis));
    }


}