
package com.example.alessandro.loginandroid.Activity;
import android.os.AsyncTask;
        import android.os.Bundle;
        import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.ListView;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Adapters.ListMessage;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
        import com.example.alessandro.loginandroid.Entity.Message;
        import com.example.alessandro.loginandroid.Entity.User;
        import com.example.alessandro.loginandroid.R;
        import com.google.gson.Gson;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.HttpVersion;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.message.BasicNameValuePair;
        import org.apache.http.params.BasicHttpParams;
        import org.apache.http.params.CoreProtocolPNames;
        import org.apache.http.params.HttpParams;
        import org.apache.http.util.EntityUtils;
        import org.json.JSONArray;
        import org.json.JSONException;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;

public class MessageActivity extends Activity {

    private ListView messagelist;
    private EditText text;
    private ImageView send;
    private ClientLocalStore clientLocalStore;
    private ArrayList<Message> messages;
    String userName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);



        messagelist = (ListView)findViewById(R.id.message_messagelist_lv);
        text = (EditText)findViewById(R.id.message_text_et);
        send = (ImageView)findViewById(R.id.message_send_iv);
        clientLocalStore = new ClientLocalStore(this);

        Bundle bundle = getIntent().getExtras();
        final String userEmail = bundle.getString("userEmail");
        userName = bundle.getString("userName");
        final String myEmail = clientLocalStore.getUser().getEmail();
        initToolbar();

        messages = new ArrayList<>();
        new GetConversation(userEmail,myEmail).execute();
        new SetRead(myEmail, userEmail).execute();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String writtentext = text.getText().toString();
                new SendMessage(userEmail, myEmail, writtentext).execute();
            }
        });


    }
    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolBarTextView.setText(userName);
    }

    public void setAdapter(){
        ListMessage adapter = new ListMessage(this, clientLocalStore.getUser(), messages);
        messagelist.setAdapter(adapter);
        messagelist.deferNotifyDataSetChanged();
        messagelist.setSelection(adapter.getCount()-1);
    }

    public class GetConversation extends AsyncTask<Void,Void,Void> {
        private String userEmail;
        private String myEmail;
        private JSONArray usersArray;

        public GetConversation(String userEmail,String myEmail){
            this.userEmail=userEmail;
            this.myEmail=myEmail;}

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            messages = new ArrayList<>();
            for (int i = 0; i < usersArray.length(); i++) {
                Message message = null;
                try {
                    message = new Gson().fromJson(usersArray.get(i).toString(), Message.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                messages.add(message);
                setAdapter();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/getConversation");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user_mail", userEmail));
                nameValuePairs.add(new BasicNameValuePair("my_mail", myEmail));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity =response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println(json);
                usersArray = new JSONArray(json);
                System.out.println(json);


            }catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class SendMessage extends AsyncTask<Void, Void, Void>{

        private String userEmail;
        private String myEmail;
        private String writtentext;


        public SendMessage(String userEmail, String myEmail, String text) {
            this.userEmail = userEmail;
            this.myEmail = myEmail;
            this.writtentext = text;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new GetConversation(userEmail, myEmail).execute();
            text.setText("");
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/addMessage");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("my_mail", myEmail));
                nameValuePairs.add(new BasicNameValuePair("user_mail", userEmail));
                nameValuePairs.add(new BasicNameValuePair("text",writtentext));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);



            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class SetRead extends AsyncTask<String, Void, Void> {

        // attributi necessari per il login  tramite refresh
        private String my_mail;
        private String user_mail;

        //costruttore

        public SetRead(String my_mail, String user_mail) {
            this.my_mail = my_mail;
            this.user_mail = user_mail;
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
                nameValuePairs.add(new BasicNameValuePair("my_mail", my_mail));
                nameValuePairs.add(new BasicNameValuePair("user_mail", user_mail));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity =response.getEntity();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}