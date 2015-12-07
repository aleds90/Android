package com.example.alessandro.loginandroid.Test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Activity.LoginActivity;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che gestisce l'activity del profilo, un utente potra' cambiare i propri dati, cancellare
 * il proprio profilo o fare logout.
 */
public class testProfileActivity extends Activity implements View.OnClickListener {
    //bottoni per cambiare activity
    ImageButton buttonHOME, buttonSEARCH, buttonFOLLOW, buttonPROFILE;
    //bottoni dei comandi specifici per questa activity
    private Button profileLogoutButton;
    private Button profileDeleteButton;
    //bottoni per l'update
    private CheckBox profileUpdateCheckBox;
    private Button profileUpdateButton;
    //database che contiene i dati di login dell' utente
    private ClientLocalStore clientLocalStore;
    //views che identificano i dati dell'utente
    private TextView profileNameEditText;
    private TextView profileSurnameEditText;
    private TextView profileBdayEditText;
    private TextView profileCityEditText;
    private TextView profilePasswordEditText;
    private TextView profileEmailEditText;
    private TextView profileRoleEditText;
    private TextView profileRateEditText;
    private TextView textViewPROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_profile);

        buttonHOME = (ImageButton) findViewById(R.id.buttonHOME);
        buttonSEARCH = (ImageButton) findViewById(R.id.buttonSEARCH);
        buttonFOLLOW = (ImageButton) findViewById(R.id.buttonFOLLOW);
        buttonPROFILE = (ImageButton) findViewById(R.id.buttonPROFILE);
        textViewPROFILE=(TextView)findViewById(R.id.textViewPROFILE);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/cartoon.ttf");
        textViewPROFILE.setTypeface(typeface);
        buttonHOME.setOnClickListener(this);
        buttonSEARCH.setOnClickListener(this);
        buttonFOLLOW.setOnClickListener(this);
        buttonPROFILE.setOnClickListener(this);


        profileLogoutButton = (Button) findViewById(R.id.profileLogoutButton);
        profileDeleteButton = (Button) findViewById(R.id.profileDeleteButton);
        profileUpdateCheckBox = (CheckBox) findViewById(R.id.profileUpdateCheckBox);
        profileUpdateButton = (Button) findViewById(R.id.profileUpdateButton);

        profileLogoutButton.setOnClickListener(this);
        profileDeleteButton.setOnClickListener(this);

        profileUpdateButton.setOnClickListener(this);

        profileNameEditText = (TextView) findViewById(R.id.profileNameEditText);
        profileSurnameEditText = (TextView) findViewById(R.id.profileSurnameEditText);
        profileBdayEditText = (TextView) findViewById(R.id.profileBdayEditText);
        profileCityEditText = (TextView) findViewById(R.id.profileCityEditText);
        profilePasswordEditText = (TextView) findViewById(R.id.profilePasswordEditText);

        profileRoleEditText = (TextView) findViewById(R.id.profileRoleEditText);
        profileRateEditText = (TextView) findViewById(R.id.profileRateEditText);

        clientLocalStore = new ClientLocalStore(this);
        User user = clientLocalStore.getUser();
        profileNameEditText.setText(user.getName());
        profileSurnameEditText.setText(user.getSurname());
        profileBdayEditText.setText(user.getBday());
        profileCityEditText.setText(user.getCity());
        profilePasswordEditText.setText(user.getPassword());

        profileRoleEditText.setText(user.getRole());
        profileRateEditText.setText(String.valueOf(user.getRate()));
        profileUpdateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    profileNameEditText.setFocusable(true);
                    profileNameEditText.setFocusableInTouchMode(true);

                    profileSurnameEditText.setFocusable(true);
                    profileSurnameEditText.setFocusableInTouchMode(true);


                    profileBdayEditText.setFocusable(true);
                    profileBdayEditText.setFocusableInTouchMode(true);

                    profileCityEditText.setFocusable(true);
                    profileCityEditText.setFocusableInTouchMode(true);

                    profilePasswordEditText.setFocusable(true);
                    profilePasswordEditText.setFocusableInTouchMode(true);



                    profileRoleEditText.setFocusable(true);
                    profileRoleEditText.setFocusableInTouchMode(true);

                    profileRateEditText.setFocusable(true);
                    profileRateEditText.setFocusableInTouchMode(true);

                } else {
                    profileNameEditText.setFocusable(false);
                    profileNameEditText.setFocusableInTouchMode(false);

                    profileSurnameEditText.setFocusable(false);
                    profileSurnameEditText.setFocusableInTouchMode(false);

                    profileBdayEditText.setFocusable(false);
                    profileBdayEditText.setFocusableInTouchMode(false);

                    profileCityEditText.setFocusable(false);
                    profileCityEditText.setFocusableInTouchMode(false);

                    profilePasswordEditText.setFocusable(false);
                    profilePasswordEditText.setFocusableInTouchMode(false);



                    profileRoleEditText.setFocusable(false);
                    profileRoleEditText.setFocusableInTouchMode(false);

                    profileRateEditText.setFocusable(false);
                    profileRateEditText.setFocusableInTouchMode(false);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.buttonHOME):
                Intent intent = new Intent(this, testMainActivity.class);
                startActivity(intent);
                break;
            case (R.id.buttonSEARCH):
                Intent intent1 = new Intent(this, testSearchActivity.class);
                startActivity(intent1);

                break;
            case (R.id.buttonFOLLOW):
                Intent intent2 = new Intent(this, testFollowActivity.class);
                startActivity(intent2);
                break;
            case (R.id.buttonPROFILE):
                Intent intent3 = new Intent(this, testProfileActivity.class);
                startActivity(intent3);
                break;
            case R.id.profileLogoutButton:
                clientLocalStore.clearClient();
                Intent intent5 = new Intent(this, LoginActivity.class);
                startActivity(intent5);
                break;
            case R.id.profileDeleteButton:
                new deleteTask(clientLocalStore.getUser()).execute();
                Intent intent6 = new Intent(this, LoginActivity.class);
                startActivity(intent6);
                break;
            case R.id.profileUpdateButton:
                User user = new User();
                user.setId_user(clientLocalStore.getUser().getId_user());
                user.setName(profileNameEditText.getText().toString());
                user.setSurname(profileSurnameEditText.getText().toString());
                user.setCity(profileCityEditText.getText().toString());
                user.setRate(Double.parseDouble(profileRateEditText.getText().toString()));
                user.setRole(profileRoleEditText.getText().toString());

                user.setPassword(profilePasswordEditText.getText().toString());
                System.out.println(user.getId_user());
                System.out.println(user.getName());
                System.out.println(user.getSurname());
                System.out.println(user.getCity());
                System.out.println(user.getRate());
                System.out.println(user.getRole());
                System.out.println(user.getPassword());

                new updateTask(user).execute();
        }
    }


    /**
     * Classe utilizzata per richiede al server la cancellazione di uno user.
     */
    public class deleteTask extends AsyncTask<Void, Void, Void> {
        private User user;

        private deleteTask(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/delete");

                List<NameValuePair> nameValuePairs = new ArrayList<>(1);

                nameValuePairs.add(new BasicNameValuePair("id_user", Integer.toString(user.getId_user())));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);

                System.out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class updateTask extends AsyncTask<Void, Void, Void> {

        private User user;

        public updateTask(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/update");

                List<NameValuePair> nameValuePairs = new ArrayList<>(7);

                nameValuePairs.add(new BasicNameValuePair("id_user", Integer.toString(user.getId_user())));
                nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
                nameValuePairs.add(new BasicNameValuePair("surname", user.getSurname()));
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("role", user.getRole()));
                nameValuePairs.add(new BasicNameValuePair("city", user.getCity()));
                nameValuePairs.add(new BasicNameValuePair("rate", Double.toString(user.getRate())));


                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                String response = EntityUtils.toString(httpEntity);
                System.out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}