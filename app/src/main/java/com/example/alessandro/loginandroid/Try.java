package com.example.alessandro.loginandroid;

/**
 * Created by root on 06/11/15.
 */
public class Try {

    public static void main(String[] args){
        User user = new User();
        user.setEmail("sjnao@sjnao.sjnao");
        user.setPassword("njsao");
        Client client = new Client(user, "AT", "RT", "Password");
        client.login();
    }
}
