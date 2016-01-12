package com.example.alessandro.loginandroid.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Activity.MessageActivity;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Message;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

import java.util.ArrayList;

public class ListUserConversations extends ArrayAdapter<User> {

    private final Context context;
    private final ArrayList<User> users;
    private final ArrayList<Message> messages;
    private ClientLocalStore clientLocalStore;


    /**
     * Costruttore
     * @param context per capire a che Activity/Listview si riferisce
     * @param users lista di utenti che verrá visualizzata
     */
    public ListUserConversations(Context context, ArrayList<User> users, ArrayList<Message> messages) {
        super(context, R.layout.list_message, users);
        this.context = context;
        this.users = users;
        this.messages = messages;

    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_message, parent, false);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.goToMessage);
        TextView name       = (TextView)rowView.findViewById(R.id.conversationUserNameText);
        TextView surname    =(TextView)rowView.findViewById(R.id.conversationUserSurnameText);
        TextView lastMessage =(TextView)rowView.findViewById(R.id.lastMessageText);
        clientLocalStore = new ClientLocalStore(getContext());


        final Message currentMessage = messages.get(position);
        final User currentUser = users.get(position);
        name.setText(currentUser.getName());
        surname.setText(currentUser.getSurname());
        lastMessage.setText(currentMessage.getText());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), MessageActivity.class);
                intent.putExtra("userEmail", currentUser.getEmail());
                intent.putExtra("userName", currentUser.getName());
                getContext().startActivity(intent);
            }
        });

        return rowView;
    }



}
