package com.example.alessandro.loginandroid.Entity;




import java.util.Date;

/**
 * Created by jns on 30/11/15.
 */

public class Message {

    private int id_message;
    private String text;
    private Date sendetAt;
    private boolean read;
    private User id_sender;
    private User id_receiver;


    public Message(){}


    public int getId_message() {
        return id_message;
    }

    public void setId_message(int id_message) {
        this.id_message = id_message;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getSendetAt() {
        return sendetAt;
    }

    public void setSendetAt(Date sendetAt) {
        this.sendetAt = sendetAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }


    public User getId_sender() {
        return id_sender;
    }

    public void setId_sender(User id_sender) {
        this.id_sender = id_sender;
    }


    public User getId_receiver() {
        return id_receiver;
    }

    public void setId_receiver(User id_receiver) {
        this.id_receiver = id_receiver;
    }

}
