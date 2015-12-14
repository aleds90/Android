package com.example.alessandro.loginandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Entity.Client;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Message;
import com.example.alessandro.loginandroid.Entity.Response;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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
                new setRead(clientLocalStore.getUser().getId_user(),currentUser.getId_user()).execute();
                Intent intent=new Intent(getContext(), MessageActivity.class);
                intent.putExtra("userEmail", currentUser.getEmail());
                getContext().startActivity(intent);
            }
        });

        return rowView;
    }

    public class setRead extends AsyncTask<String, Void, Void> {

        // attributi necessari per il login  tramite refresh
        private int reicever;
        private int sender;

        //costruttore
        public setRead(int reicever,int sender) {
            this.reicever = reicever;
            this.sender = sender;
        }

        /*
         * Lavoro che svolge in background la classe Task. Invia la richiesta ad un indirizzo e riceve una risposta da quest'ultimo
         */
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/setRead");
                // Lista dei valori che mandiamo
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("receiver",String.valueOf(reicever)));
                nameValuePairs.add(new BasicNameValuePair("sender", String.valueOf(sender)));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity =response.getEntity();
                String json = EntityUtils.toString(entity);
                JSONArray usersArray = new JSONArray(json);
                System.out.println(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
