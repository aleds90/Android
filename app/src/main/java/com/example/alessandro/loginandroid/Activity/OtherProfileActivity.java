package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Visulizza i dettagli di un profilo
 */
public class OtherProfileActivity extends Activity implements View.OnClickListener {

    TextView tvNome, tvCognome, tvEmail, tvRuolo, tvIndirizzo, tvBday, tvTariffa;
    Button btnSendMessageOtherProfile, btnFollowOtherProfile;
    User user, target;
    ClientLocalStore clientLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        tvNome = (TextView) findViewById(R.id.tvNome);
        tvCognome = (TextView) findViewById(R.id.tvCognome);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvRuolo = (TextView) findViewById(R.id.tvRuolo);
        tvIndirizzo = (TextView) findViewById(R.id.tvIndirizzo);
        tvBday = (TextView) findViewById(R.id.tvBday);
        tvTariffa = (TextView) findViewById(R.id.tvRate);
        btnSendMessageOtherProfile = (Button)findViewById(R.id.btnSendMessageOtherProfile);
        btnFollowOtherProfile = (Button)findViewById(R.id.btnFollowOtherProfile);


        Bundle bundle = getIntent().getExtras();
        target = getUserBybundle(bundle);

        clientLocalStore = new ClientLocalStore(this);
        user = clientLocalStore.getUser();

        new CheckFollowTask(user, target).execute();


        tvNome.setText("Nome: " + target.getName());
        tvCognome.setText("Cognome: " + target.getSurname());
        tvEmail.setText("Email: " + target.getEmail());
        tvRuolo.setText(target.getRole());
        tvIndirizzo.setText("Zona: " + target.getCity());
        tvBday.setText("nato/a il: " + target.getBday());
        tvTariffa.setText("tariffa: " + target.getRate() + "/h");
        Log.d("ID_USER", "" + target.getId_user());

        btnSendMessageOtherProfile.setOnClickListener(this);

        btnFollowOtherProfile.setOnClickListener(this);
    }

    private User getUserBybundle(Bundle bundle) {
        int id_user = bundle.getInt("id_user");
        String name = bundle.getString("name");
        String cognome = bundle.getString("cognome");
        String city = bundle.getString("city");
        String email = bundle.getString("email");
        String bday = bundle.getString("bday");
        String role = bundle.getString("role");
        Double rate = bundle.getDouble("rate");
        return new User(id_user, name, cognome, email, "", bday, role, city, rate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSendMessageOtherProfile:
                Intent intent = new Intent(this, MessageActivity.class);
                intent.putExtra("userEmail", target.getEmail());
                startActivity(intent);
                break;
            case R.id.btnFollowOtherProfile:
                new AddFollowTask(user, target).execute();
                break;
        }
    }


    public class CheckFollowTask extends AsyncTask<Void, Void, Void> {
        User user;
        User target;
        boolean isFollower;


        public CheckFollowTask(User user, User target) {
            this.user = user;
            this.target = target;
        }

            @Override
            protected void onPostExecute (Void aVoid){
                super.onPostExecute(aVoid);
                if (isFollower) {
                    btnFollowOtherProfile.setText("Unfollow");
                } else {
                    btnFollowOtherProfile.setText("Follow");
                }

            }

            @Override
            protected Void doInBackground (Void...params){
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    // sito a cui fare il post
                    HttpPost httppost = new HttpPost("http://10.0.2.2:4567/checkFollow");
                    // Lista dei valori che mandiamo
                    List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                    // Valori:
                    nameValuePairs.add(new BasicNameValuePair("emailUser", user.getEmail()));
                    nameValuePairs.add(new BasicNameValuePair("emailTarget", target.getEmail()));

                    //Mettere la lista nel post, cosi da poterla mandare
                    // prima l aveva solo creata ma non settata come da mandare
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                    // con l execute, mandiamo il post
                    HttpResponse response = httpclient.execute(httppost);
                    // prendiamo la risposta del server e lo salviamo come stringa in "json"
                    HttpEntity entity = response.getEntity();

                    String json = EntityUtils.toString(entity);

                    isFollower = json.equals("true");


                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

        }


    public class AddFollowTask extends AsyncTask<Void, Void, Void> {
        User user;
        User target;
        String newText;

        public AddFollowTask(User user, User target) {
            this.user = user;
            this.target = target;
        }

        @Override
        protected void onPostExecute (Void aVoid){
            super.onPostExecute(aVoid);
            btnFollowOtherProfile.setText(newText);

        }

        @Override
        protected Void doInBackground (Void...params){
            try {

                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/addFollow");
                // Lista dei valori che mandiamo
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("emailUser", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("emailTarget", target.getEmail()));

                //Mettere la lista nel post, cosi da poterla mandare
                // prima l aveva solo creata ma non settata come da mandare
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                // con l execute, mandiamo il post
                HttpResponse response = httpclient.execute(httppost);
                // prendiamo la risposta del server e lo salviamo come stringa in "json"
                HttpEntity entity = response.getEntity();

                newText = EntityUtils.toString(entity);



            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }



}
