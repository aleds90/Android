package com.example.alessandro.loginandroid.Activity;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Entity.Message;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListMessage extends ArrayAdapter<Message> {

    private final Context context;
    private final User user;
    private final ArrayList<Message> messages;


    /**
     * Costruttore
     * @param context per capire a che Activity/Listview si riferisce
     * @param user lista di utenti che verrá visualizzata
     */
    public ListMessage(Context context, User user, ArrayList<Message> messages) {
        super(context, R.layout.list_test_message, messages);
        this.context = context;
        this.user = user;
        this.messages = messages;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_test_message, parent, false);
        TextView textMessage = (TextView)rowView.findViewById(R.id.textMessage);
        TextView timeMessage = (TextView)rowView.findViewById(R.id.timeMessage);

        final Message currentMessage = messages.get(position);

        textMessage.setText(currentMessage.getText());
        DateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a");
        String myDate = dateFormat.format(currentMessage.getSendetAt());
        timeMessage.setText(myDate);
        if (user.getId_user() == currentMessage.getId_sender().getId_user()){
            textMessage.setGravity(Gravity.RIGHT);
            timeMessage.setGravity(Gravity.LEFT);
        }
        return rowView;
    }


}