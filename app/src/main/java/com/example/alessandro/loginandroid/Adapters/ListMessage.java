package com.example.alessandro.loginandroid.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Activity.OtherProfileActivity;
import com.example.alessandro.loginandroid.Activity.ProfileActivity;
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
        ImageView imageView = (ImageView)rowView.findViewById(R.id.image_message);

        final Message currentMessage = messages.get(position);
        String role = user.getRole();
        int avatarInt = user.getAvatar();
        String email = user.getEmail();
        String url = "http://njsao.pythonanywhere.com/static/"+email+".png";


        new ProfileActivity().getDrawableAvatar(role, avatarInt, imageView, getContext(), url);
        textMessage.setText(currentMessage.getText());
        timeMessage.setText(currentMessage.getSendetAt().toString());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent);
            }
        });



        if (user.getId_user() != currentMessage.getId_sender().getId_user()){
            final float scale = rowView.getResources().getDisplayMetrics().density;
            int padding_50dp = (int) (50 * scale + 0.5f);
            int padding_55dp = (int) (55 * scale + 0.5f);
            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(padding_50dp, padding_50dp);
            imageView.setLayoutParams(layoutParams);
            String role2 = currentMessage.getId_sender().getRole();
            int avatarInt2 = currentMessage.getId_sender().getAvatar();
            String email2 = currentMessage.getId_sender().getEmail();
            String url2 = "http://njsao.pythonanywhere.com/static/"+email2+".png";

            new ProfileActivity().getDrawableAvatar(role2, avatarInt2, imageView, getContext(), url);
            RelativeLayout.LayoutParams layoutParams1=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(padding_55dp, 0, 0, 0);
            textMessage.setLayoutParams(layoutParams1);
            timeMessage.setGravity(Gravity.RIGHT);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    passUserByIntent(intent, user);
                    context.startActivity(intent);
                }
            });

        }
        return rowView;
    }

    private void passUserByIntent(Intent intent, User user) {
        intent.putExtra("id_user", user.getId_user());
        intent.putExtra("name", user.getName());
        intent.putExtra("cognome", user.getSurname());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("city", user.getCity());
        intent.putExtra("role", user.getRole());
        intent.putExtra("bday", user.getBday());
        intent.putExtra("rate", user.getRate());
        intent.putExtra("status", user.isActive());
        intent.putExtra("description",user.getDescription());
    }
}