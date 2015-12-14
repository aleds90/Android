
        package com.example.alessandro.loginandroid.Activity;

        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.app.Activity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.ListView;

        import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
        import com.example.alessandro.loginandroid.Entity.Message;
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
        import org.json.JSONException;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;

public class MessageActivity extends Activity {

    private ListView messageListView;
    private EditText typeMessageEditText;
    private ImageView sendMessageButton;
    private ClientLocalStore clientLocalStore;
    private User user;
    private ArrayList<Message> messages;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageListView = (ListView)findViewById(R.id.messageListView);
        typeMessageEditText = (EditText)findViewById(R.id.typeMessageEditText);
        sendMessageButton = (ImageView)findViewById(R.id.sendMessageButton);
        clientLocalStore = new ClientLocalStore(this);

        Bundle bundle = getIntent().getExtras();
        final String userEmail = bundle.getString("userEmail");
        final String myEmail = clientLocalStore.getUser().getEmail();

        messages = new ArrayList<>();
        new GetConversation(userEmail,myEmail).execute();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = typeMessageEditText.getText().toString();
                new SendMessage(userEmail, myEmail, text).execute();
            }
        });


    }

    public void setAdapter(){
        ListMessage adapter = new ListMessage(this, clientLocalStore.getUser(), messages);
        messageListView.setAdapter(adapter);
        messageListView.deferNotifyDataSetChanged();
        messageListView.setSelection(adapter.getCount()-1);
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
                HttpClient httpClient = new DefaultHttpClient();
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
        private String text;


        public SendMessage(String userEmail, String myEmail, String text) {
            this.userEmail = userEmail;
            this.myEmail = myEmail;
            this.text = text;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new GetConversation(userEmail, myEmail).execute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/addMessage");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("my_mail", myEmail));
                nameValuePairs.add(new BasicNameValuePair("user_mail", userEmail));
                nameValuePairs.add(new BasicNameValuePair("text",text));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);



            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
