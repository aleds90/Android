package com.example.alessandro.loginandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

import java.util.ArrayList;


/**
 * Created by sjnao on 11/13/15.
 */
public class UserArrayAdapter extends ArrayAdapter<User> {

    private final Context context;
    private final ArrayList<User> users;

    public UserArrayAdapter(Context context, ArrayList<User> users) {
        super(context, R.layout.user_layout, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.user_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.tvUser);
        Button button = (Button) rowView.findViewById(R.id.bProfilo);
        final User currentUser = users.get(position);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OtherProfileActivity.class);
                passUserByIntent(intent, currentUser);
                context.startActivity(intent);
            }
        });

        textView.setText(currentUser.getName() + " - " + currentUser.getRole());
        return rowView;
    }

    private void passUserByIntent(Intent intent, User user) {
        intent.putExtra("name", user.getName());
        intent.putExtra("cognome", user.getSurname());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("city", user.getCity());
        intent.putExtra("role", user.getRole());
        intent.putExtra("bday", user.getBday());
        intent.putExtra("rate", user.getRate());
    }
}
