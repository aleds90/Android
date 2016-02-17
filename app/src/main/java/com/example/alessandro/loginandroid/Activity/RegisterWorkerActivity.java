package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.roomorama.caldroid.CaldroidFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Classe che si occupa di gestire l'interfaccia per la registrazione
 */
public class RegisterWorkerActivity extends Activity implements View.OnClickListener{
    EditText name, surname, email, password, bday, city, rate;
    Button register, menu;
    Spinner roles;
    CheckBox terms;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_register);
        setViews();
        dateFormatter = new SimpleDateFormat("yyyy-dd-MM", Locale.ITALY);

        findViewsById();

        setDateTimeField();

    }
    private void findViewsById() {
        bday        = (EditText) findViewById(R.id.register_bday);
        bday.setInputType(InputType.TYPE_NULL);
        bday.requestFocus();

    }

    private void setDateTimeField() {
        bday.setOnClickListener(this);


        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                bday.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Metodo che inizializza tutti le componenti della Register Activity
     */
    private void setViews() {
        name        = (EditText) findViewById(R.id.register_name);
        surname     = (EditText) findViewById(R.id.register_surname);
        email       = (EditText) findViewById(R.id.register_email);
        password    = (EditText) findViewById(R.id.register_password);

        roles       = (Spinner)findViewById(R.id.register_roles);
        city        = (EditText) findViewById(R.id.register_city);
        rate        = (EditText) findViewById(R.id.register_rate);
        register    = (Button) findViewById(R.id.register_register);
        menu        = (Button) findViewById(R.id.register_menu);
        terms       = (CheckBox)findViewById(R.id.register_terms_worker);

        register.setOnClickListener(this);
        menu.setOnClickListener(this);
    }

   /**
    *Definisco le azioni che ogni ogni componente con ClickListener settato deve effettuare al suo click
    */
    @Override
    public void onClick(View view) {
        if(view == bday) {
            fromDatePickerDialog.show();
        }
        switch (view.getId()) {
            case R.id.register_register:
                String name_label = name.getText().toString();
                String surname_label = surname.getText().toString();
                String email_label = email.getText().toString();
                String password_label = password.getText().toString();
                String bday_label = bday.getText().toString();
                String role_label = roles.getSelectedItem().toString();
                String city_label = city.getText().toString();
                String rate_label = rate.getText().toString();
                if (name_label.equals("")||surname_label.equals("")||email_label.equals("")||
                        password_label.equals("")||bday_label.equals("")||role_label.equals("")||
                        city_label.equals("")||rate_label.equals("")||!terms.isChecked()){
                    Toast.makeText(getApplicationContext(),getResources().getText(
                            R.string.register_toast),Toast.LENGTH_LONG).show();
                }else {
                    User user = new User();
                    user.setName(name_label);
                    user.setSurname(surname_label);
                    user.setEmail(email_label);
                    user.setPassword(password_label);
                    user.setBday(bday_label);
                    user.setRole(role_label);
                    user.setCity(city_label);
                    user.setRate(Double.parseDouble(rate_label));
                    user.setAvatar(1);
                    user.setDescription("Inserisci una descrizione");
                    new RegisterTask(user).execute();
                }
                break;
            case R.id.register_menu:
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
                nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
                nameValuePairs.add(new BasicNameValuePair("description", user.getDescription()));
                nameValuePairs.add(new BasicNameValuePair("surname", user.getSurname()));
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("bday", user.getBday()));
                nameValuePairs.add(new BasicNameValuePair("role", user.getRole()));
                nameValuePairs.add(new BasicNameValuePair("city", user.getCity()));
                nameValuePairs.add(new BasicNameValuePair("rate", Double.toString(user.getRate())));
                nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
                nameValuePairs.add(new BasicNameValuePair("avatar", Integer.toString(user.getAvatar())));
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
                Toast.makeText(RegisterWorkerActivity.this, "Register Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}