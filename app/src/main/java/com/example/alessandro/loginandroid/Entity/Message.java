package com.example.alessandro.loginandroid.Entity;




import java.security.Timestamp;
import java.util.Date;

/**
 * Created by jns on 30/11/15.
 */

public class Message {

    private int id_message;
    private String text;
    private String sendet_at;
    private boolean isread;
    private User sender_id;
    private User receiver_id;


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

    public String getSendetAt() {
        return sendet_at;
    }

    public void setSendetAt(String sendetAt) {
        this.sendet_at = sendetAt;
    }

    public boolean isRead() {
        return isread;
    }

    public void setRead(boolean read) {
        this.isread = read;
    }


    public User getId_sender() {
        return sender_id;
    }

    public void setId_sender(User id_sender) {
        this.sender_id = id_sender;
    }


    public User getId_receiver() {
        return receiver_id;
    }

    public void setId_receiver(User id_receiver) {
        this.receiver_id = id_receiver;
    }

}
