package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alessandro.loginandroid.Adapters.ListUser;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Notice;
import com.example.alessandro.loginandroid.Entity.Response;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.google.gson.Gson;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Classe che gestisce l'activity del profilo, un utente potra' cambiare i propri dati, cancellare
 * il proprio profilo o fare logout.
 */
public class testProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long RIPPLE_DURATION = 250;
    private EditText name,surname, feedback, followers, followed, role, city, bday, email, password, rate, description_tv, notice_tv;
    private ImageButton home, search, follow, profile;
    private Button update, cancel;
    //database che contiene i dati di login dell' utente
    private ClientLocalStore clientLocalStore;
    final String NEW_FORMAT = "dd-MM-yyyy";
    ImageView hamburger;
    Toolbar toolbar;
    FrameLayout root;
    TextView logout,delete,settings,status;


    //views che identificano i dati dell'utente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_profile);
        //carico lo user con cui sono loggato
        clientLocalStore = new ClientLocalStore(this);
        User user = clientLocalStore.getUser();
        // inizializzo questi due textview prima delle task
        description_tv = (EditText) findViewById(R.id.profile_description_tv);
        notice_tv = (EditText) findViewById(R.id.profile_notice_tv);
        //lancio la task per prendere l ultimo annuncio scritto
        new NoticeTask(user).execute();
        //lancio task per conteggio dei feedback e followers
        new FeedBackTask(user).execute();
        //inizializza i textview
        setTextView(user);
        //inizializzo i bottoni
        setButtons();
        createToolbar();
    }

    private void createToolbar() {
        root=(FrameLayout)findViewById(R.id.root);
        toolbar = (Toolbar)findViewById(R.id.toolbar_hamburger);
        hamburger=(ImageView)findViewById(R.id.content_hamburger);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }
        View navigationMenu = LayoutInflater.from(this).inflate(R.layout.navigation,null);
        root.addView(navigationMenu);
        new GuillotineAnimation.GuillotineBuilder(navigationMenu, navigationMenu.findViewById(R.id.guillotine_hamburger), hamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
                        //LOGOUT BUTTON
                        logout = (TextView) findViewById(R.id.logout_textview);
                        logout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clientLocalStore.clearClient();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        //DELETE BUTTON
                        delete = (TextView) findViewById(R.id.delete_textview);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //new deleteTask(clientLocalStore.getUser()).execute();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        //SETTING BUTTON
                        settings=(TextView)findViewById(R.id.setting_textview);
                        settings.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                                startActivity(intent);
                            }
                        });
                        //STATUS BUTTON
                        status=(TextView)findViewById(R.id.status_textview);
                        //status.setText("status:" + getStatus());
                        status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //updateStatus();
                                //status.setText("status:" + getStatus());
                            }
                        });



                    }

                    @Override
                    public void onGuillotineClosed() {

                    }
                })
                .build();
    }

    private void setButtons() {
        home    =(ImageButton)findViewById(R.id.profile_home_btn);
        search  =(ImageButton)findViewById(R.id.profile_search_btn);
        follow  =(ImageButton)findViewById(R.id.profile_follow_btn);
        profile =(ImageButton)findViewById(R.id.profile_profile_btn);
        update  =(Button)findViewById(R.id.profile_update_btn);
        cancel  =(Button)findViewById(R.id.profile_cancel_update_btn);
        home.setOnClickListener(this);
        search.setOnClickListener(this);
        follow.setOnClickListener(this);
        profile.setOnClickListener(this);
        update.setOnClickListener(this);
        cancel.setOnClickListener(this);
        cancel.setFocusable(false);
        cancel.setFocusableInTouchMode(false);
    }

    private void setTextView(User user) {
        name        =(EditText)findViewById(R.id.profile_name_tv);
        surname     =(EditText)findViewById(R.id.profile_surname_tv);
        feedback    =(EditText)findViewById(R.id.profile_feedback_tv);
        followers   =(EditText)findViewById(R.id.profile_followers_tv);
        followed    =(EditText)findViewById(R.id.profile_followed_tv);
        role        =(EditText)findViewById(R.id.profile_role_tv);
        city        =(EditText)findViewById(R.id.profile_city_tv);
        bday        =(EditText)findViewById(R.id.profile_bday_tv);
        email       =(EditText)findViewById(R.id.profile_email_tv);
        password    =(EditText)findViewById(R.id.profile_password_tv);
        rate        =(EditText)findViewById(R.id.profile_rate_tv);

        name.setText(user.getName());
        surname.setText(user.getSurname());
        role.setText(user.getRole());
        city.setText(user.getCity());
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.ITALY);
        try {
            Date date = format.parse(user.getBday());
            format.applyPattern(NEW_FORMAT);
           String newDate=format.format(date);
          System.out.println(newDate);
          bday.setText(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        email.setText(user.getEmail());
        password.setText(user.getPassword());
        rate.setText(String.valueOf(user.getRate()));
        description_tv.setText(user.getDescription());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.profile_home_btn):
                Intent intent = new Intent(this, testMainActivity.class);
                startActivity(intent);
                break;
            case (R.id.profile_search_btn):
                Intent intent1 = new Intent(this, testSearchActivity.class);
                startActivity(intent1);

                break;
            case (R.id.profile_follow_btn):
                Intent intent2 = new Intent(this, testFollowActivity.class);
                startActivity(intent2);
                break;
            case (R.id.profile_profile_btn):
                Intent intent3 = new Intent(this, testProfileActivity.class);
                startActivity(intent3);
                break;
            case R.id.profile_update_btn:
                if (update.getText().toString().equals("Modifica profilo")) {
                    setTextViewFocusable();
                    Toast.makeText(this,"Puoi modificare i dati del tuo profilo",Toast.LENGTH_SHORT).show();
                    cancel.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    cancel.setText("ANNULLA");
                    update.setText("CONFERMA");
                }else if (update.getText().toString().equals("CONFERMA")) {
                    Toast.makeText(this,"Modifiche confermate",Toast.LENGTH_SHORT).show();
                    User user = new User();
                    user.setId_user(clientLocalStore.getUser().getId_user());
                    user.setName(name.getText().toString());
                    user.setSurname(surname.getText().toString());
                    user.setCity(city.getText().toString());
                    user.setRate(Double.parseDouble(rate.getText().toString()));
                    user.setEmail(email.getText().toString());
                    user.setRole(role.getText().toString());
                    user.setPassword(password.getText().toString());
                    user.setDescription(description_tv.getText().toString());
                    String notice = notice_tv.getText().toString();
                    new updateTask(user,notice).execute();
                    cancel.setLayoutParams(new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT));
                    cancel.setText("");
                    update.setText("Modifica profilo");
                    setTextViewNotFocusable();
                }
                break;
            case R.id.profile_cancel_update_btn:
                Toast.makeText(this,"Modifiche annullate",Toast.LENGTH_SHORT).show();
                cancel.setLayoutParams(new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT));
                cancel.setText("");
                update.setText("Modifica profilo");
                setTextViewNotFocusable();
                break;
        }
    }

    private void setTextViewNotFocusable() {
        name.setFocusable(false);
        name.setFocusableInTouchMode(false);
        name.setClickable(false);

        surname.setFocusable(false);
        surname.setFocusableInTouchMode(false);
        surname.setClickable(false);

        role.setFocusable(false);
        role.setFocusableInTouchMode(false);
        role.setClickable(false);

        rate.setFocusable(false);
        rate.setFocusableInTouchMode(false);
        rate.setClickable(false);

        city.setFocusable(false);
        city.setFocusableInTouchMode(false);
        city.setClickable(false);

        bday.setFocusable(false);
        bday.setFocusableInTouchMode(false);
        bday.setClickable(false);

        password.setFocusable(false);
        password.setFocusableInTouchMode(false);
        password.setClickable(false);

        notice_tv.setFocusable(false);
        notice_tv.setFocusableInTouchMode(false);
        notice_tv.setClickable(false);

        description_tv.setFocusable(false);
        description_tv.setFocusableInTouchMode(false);
        description_tv.setClickable(false);
    }

    private void setTextViewFocusable() {
        name.setFocusable(true);
        name.setFocusableInTouchMode(true);
        name.setClickable(true);

        surname.setFocusable(true);
        surname.setFocusableInTouchMode(true);
        surname.setClickable(true);

        role.setFocusable(true);
        role.setFocusableInTouchMode(true);
        role.setClickable(true);

        rate.setFocusable(true);
        rate.setFocusableInTouchMode(true);
        rate.setClickable(true);

        city.setFocusable(true);
        city.setFocusableInTouchMode(true);
        city.setClickable(true);

        bday.setFocusable(true);
        bday.setFocusableInTouchMode(true);
        bday.setClickable(true);

        password.setFocusable(true);
        password.setFocusableInTouchMode(true);
        password.setClickable(true);

        notice_tv.setFocusable(true);
        notice_tv.setFocusableInTouchMode(true);
        notice_tv.setClickable(true);

        description_tv.setFocusable(true);
        description_tv.setFocusableInTouchMode(true);
        description_tv.setClickable(true);
    }

    public class updateTask extends AsyncTask<Void, Void, Void> {

        private User user;
        private String notice;

        public updateTask(User user, String notice) {
            this.user = user;
            this.notice = notice;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            clientLocalStore.updateUser(user);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/update");

                List<NameValuePair> nameValuePairs = new ArrayList<>(9);

                nameValuePairs.add(new BasicNameValuePair("id_user", Integer.toString(user.getId_user())));
                nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
                nameValuePairs.add(new BasicNameValuePair("surname", user.getSurname()));
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("role", user.getRole()));
                nameValuePairs.add(new BasicNameValuePair("city", user.getCity()));
                nameValuePairs.add(new BasicNameValuePair("rate", Double.toString(user.getRate())));
                nameValuePairs.add(new BasicNameValuePair("description", user.getDescription()));
                nameValuePairs.add(new BasicNameValuePair("notice",notice));

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
    public class NoticeTask extends AsyncTask<Void, Void, Void> {

        private User user;
        private Notice notice;

        public NoticeTask(User user) {
            this.user = user;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notice_tv.setText(notice.getNotice_text());
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/getNotice");
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);
                System.out.println(response);
                notice = new Gson().fromJson(response, Notice.class);
                System.out.println(notice.getNotice_text());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class FeedBackTask extends AsyncTask<Void, Void, Void> {

        private User user;
        private JSONArray list;

        public FeedBackTask(User user) {
            this.user = user;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            try {
                feedback.setText(Integer.toString(list.getInt(0)));
                followers.setText(Integer.toString(list.getInt(1)));
                followed.setText(Integer.toString(list.getInt(2)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/countFeedback");

                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair("user_email", user.getEmail()));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                String response = EntityUtils.toString(httpEntity);
                System.out.println(response);
                list = new JSONArray(response);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
