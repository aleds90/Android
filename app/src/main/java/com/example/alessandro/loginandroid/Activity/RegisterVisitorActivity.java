package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;


public class RegisterVisitorActivity extends Activity implements View.OnClickListener{
    EditText name, surname, email, password;
    Button register, menu;
    CheckBox terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_visitor_activity);
        setViews();

    }

    /**
     * Metodo che inizializza tutti le componenti della Register Activity
     */
    private void setViews() {
        name        = (EditText) findViewById(R.id.register_name_visitor);
        surname     = (EditText) findViewById(R.id.register_surname_visitor);
        email       = (EditText) findViewById(R.id.register_email_visitor);
        password    = (EditText) findViewById(R.id.register_password_visitor);
        register    = (Button) findViewById(R.id.register_register_visitor);
        menu        = (Button) findViewById(R.id.register_menu_visitor);
        terms       = (CheckBox)findViewById(R.id.checkBox_visitor);

        register.setOnClickListener(this);
        menu.setOnClickListener(this);
    }

    /**
     *Definisco le azioni che ogni ogni componente con ClickListener settato deve effettuare al suo click
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_register_visitor:
                String name_label = name.getText().toString();
                String surname_label = surname.getText().toString();
                String email_label = email.getText().toString();
                String password_label = password.getText().toString();
                if (name_label.equals("")||surname_label.equals("")||email_label.equals("")||
                        password_label.equals("")||!terms.isChecked()){
                    Toast.makeText(getApplicationContext(),getResources().getText(
                            R.string.register_toast),Toast.LENGTH_LONG).show();
                }else {
                    User user = new User();
                    user.setName(name_label);
                    user.setSurname(surname_label);
                    user.setEmail(email_label);
                    user.setPassword(password_label);

                    new RegisterTask(user).execute();
                }
                break;

            case R.id.register_menu_visitor:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * classe Task che in Background manda un post ad /add per effettuare la registrazione sul server
     */
    public class RegisterTask extends AsyncTask<String, Void, Void> {
        private User user;


        public RegisterTask(User user) {
            this.user= user;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://njsao.pythonanywhere.com/insert_user");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("name", user.getName()));

                nameValuePairs.add(new BasicNameValuePair("surname", user.getSurname()));
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));

                nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println(json);
                handleResponse(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    /**
     * Gestisce la risposta del Server
     * @param responseServer
     */
    private void handleResponse(String responseServer) {
        switch (responseServer){
            //case OK, il server ci dice che la registrazione e' stata correttamente eseguita e ci rimanda alla schermata di login
            case "OK":
                Intent intent =  new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            //case NO, indica il caso in cui la registrazione non e' andata a buon fine per probabile errori dell'utente
            default:
                Toast.makeText(RegisterVisitorActivity.this, "Register Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}