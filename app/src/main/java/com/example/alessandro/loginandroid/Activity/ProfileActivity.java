package com.example.alessandro.loginandroid.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alessandro.loginandroid.Entity.Client;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Notice;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long RIPPLE_DURATION = 250;
    private EditText name,surname, feedback, followers, followed, role, city, bday, email, password, rate;
    private ImageButton home, search, follow, profile;
    private Button update, cancel, upload;
    private View dialogView;
    RadioButton radioButton,radioButton2;

    //database che contiene i dati di login dell' utente
    private ClientLocalStore clientLocalStore;
    final String NEW_FORMAT = "dd-MM-yyyy";
    ImageView hamburger;
    Toolbar toolbar;
    FrameLayout root;
    TextView logout,delete,settings,status,description_tv,notice_tv;
    private ImageView imageprofile;
    User user;
    String avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_profile);
        //carico lo user con cui sono loggato
        clientLocalStore = new ClientLocalStore(this);
        user = clientLocalStore.getUser();
        System.out.println(user.getBday());
        // inizializzo questi due textview prima delle task
        description_tv = (TextView) findViewById(R.id.profile_description_tv);
        notice_tv = (TextView) findViewById(R.id.profile_notice_tv);
        notice_tv.setOnClickListener(this);
        //lancio la task per prendere l ultimo annuncio scritto
        //new NoticeTask(user).execute();
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
                                //// TODO: 28/01/2016 Abilitare il delete
                                //new deleteTask(clientLocalStore.getUser()).execute();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        //SETTING BUTTON
                        settings = (TextView) findViewById(R.id.setting_textview);
                        settings.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                                startActivity(intent);
                            }
                        });

                        //STATUS BUTTON
                        status = (TextView) findViewById(R.id.status_textview);
                        if (clientLocalStore.getUser().isActive()) {
                            status.setText(getResources().getString(R.string.status_available));
                        } else {
                            status.setText(getResources().getString(R.string.status_not_available));
                        }
                        status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (status.getText().equals(getResources().getString(R.string.status_available))) {
                                    status.setText(getResources().getString(R.string.status_not_available));
                                    User user = clientLocalStore.getUser();
                                    user.setActive(false);
                                    new UpdateStatusTask(user).execute();
                                } else {
                                    status.setText(getResources().getString(R.string.status_available));
                                    User user = clientLocalStore.getUser();
                                    user.setActive(true);
                                    new UpdateStatusTask(user).execute();
                                }
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
        home    =(ImageButton)findViewById(R.id.home);
        search  =(ImageButton)findViewById(R.id.search);
        follow  =(ImageButton)findViewById(R.id.relation);
        profile =(ImageButton)findViewById(R.id.profile);
        home.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_white_24dp_xhdpi));
        search.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp_xhdpi));
        follow.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_white_24dp_xhdpi));
        profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_24dp));
        update  =(Button)findViewById(R.id.profile_update_btn);

        imageprofile = (ImageView)findViewById(R.id.profile_imageprofile_iv);
        String role = clientLocalStore.getUser().getRole();
        int avatar = clientLocalStore.getUser().getAvatar();
        String email = clientLocalStore.getUser().getEmail();
        String url = "http://njsao.pythonanywhere.com/static/"+email+".png";
        user.getDrawableAvatar(role, avatar, imageprofile, this, url);

        upload = (Button)findViewById(R.id.profile_upload_btn);
        upload.setOnClickListener(this);
        home.setOnClickListener(this);
        search.setOnClickListener(this);
        follow.setOnClickListener(this);
        profile.setOnClickListener(this);
        update.setOnClickListener(this);

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
        rate =(EditText)findViewById(R.id.profile_rate_tv);
        name.setText(user.getName());
        surname.setText(user.getSurname());
        role.setText(user.getRole());
        city.setText(user.getCity());
        if (clientLocalStore.getUser().getRole()==""){
            TextView rateleft = (TextView)findViewById(R.id.rate_left_bord);
            TextView rateright = (TextView)findViewById(R.id.rate_right_bord);
            rateleft.setVisibility(View.INVISIBLE);
            rateright.setVisibility(View.INVISIBLE);
            rate.setVisibility(View.INVISIBLE);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        try {
            if (user.getBday() == ""){

            }
            else {
                Date date = format.parse(user.getBday());
                format.applyPattern(NEW_FORMAT);
                String newDate = format.format(date);

                System.out.println(newDate);

                bday.setText(newDate);
            }
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
            case (R.id.profile_notice_tv):
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
                builder1.setTitle(getResources().getString(R.string.add_notice));
                LayoutInflater inflater1 = this.getLayoutInflater();
                dialogView = inflater1.inflate(R.layout.add_notice_layout, null);
                builder1.setView(dialogView);
                final EditText add_notice = (EditText)dialogView.findViewById(R.id.add_notice_edit);
                builder1.setPositiveButton("aggiungi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new NoticeTask(clientLocalStore.getUser(),add_notice.getText().toString()).execute();
                    }
                });
                builder1.setNegativeButton("annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder1.create().show();
                break;

            case (R.id.home):
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case (R.id.search):
                Intent intent1 = new Intent(this, SearchActivity.class);
                startActivity(intent1);

                break;
            case (R.id.relation):
                Intent intent2 = new Intent(this, RelationActivity.class);
                startActivity(intent2);
                break;
            case (R.id.profile):
                Intent intent3 = new Intent(this, ProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.profile_update_btn:
                    final ClientLocalStore localStore = new ClientLocalStore(getApplicationContext());
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle(getResources().getString(R.string.edit_profile));
                    LayoutInflater inflater = this.getLayoutInflater();
                    dialogView = inflater.inflate(R.layout.edit_profile_layout, null);
                    builder.setView(dialogView);
                    final EditText city_edit = (EditText)dialogView.findViewById(R.id.edit_city);
                    EditText email_edit = (EditText)dialogView.findViewById(R.id.edit_email);
                    final EditText password_edit = (EditText)dialogView.findViewById(R.id.edit_password);
                    final EditText rate_edit = (EditText)dialogView.findViewById(R.id.edit_rate);
                    final EditText description_edit = (EditText)dialogView.findViewById(R.id.edit_description);
                    final EditText notice_edit = (EditText)dialogView.findViewById(R.id.edit_notice);
                    email_edit.setText(localStore.getUser().getEmail());
                    password_edit.setText(localStore.getUser().getPassword());
                    city_edit.setText(localStore.getUser().getCity());
                    rate_edit.setText(String.valueOf(localStore.getUser().getRate()));
                    description_edit.setText(localStore.getUser().getDescription());


                    builder.setPositiveButton("salva", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            User user = new User();
                            user.setId_user(localStore.getUser().getId_user());
                            user.setName(localStore.getUser().getName());
                            user.setSurname(localStore.getUser().getSurname());
                            user.setBday(localStore.getUser().getBday());
                            user.setCity(city_edit.getText().toString());
                            user.setRate(Double.parseDouble(rate_edit.getText().toString()));
                            user.setEmail(localStore.getUser().getEmail());
                            user.setRole(localStore.getUser().getRole());
                            user.setPassword(password_edit.getText().toString());
                            user.setDescription(description_edit.getText().toString());
                            user.setAvatar(localStore.getUser().getAvatar());
                            user.setActive(localStore.getUser().isActive());
                            String notice = notice_edit.getText().toString();
                            new UpdateTask(user,notice).execute();

                        }
                    });
                    builder.setNegativeButton("annulla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();

                break;

            case R.id.profile_upload_btn:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(ProfileActivity.this);
                builder3.setTitle("choose avatar");
                LayoutInflater inflater3 = this.getLayoutInflater();
                View dialogView3 = inflater3.inflate(R.layout.choose_avatar_layout, null);
                builder3.setView(dialogView3);
                Button button = (Button)dialogView3.findViewById(R.id.buttonLoad);
                radioButton = (RadioButton)dialogView3.findViewById(R.id.radioButtonPredefinito);
                radioButton2 = (RadioButton)dialogView3.findViewById(R.id.radioButtonPersonale);
                if (user.getAvatar()==0)
                    radioButton.setChecked(true);
                else radioButton2.setChecked(true);
                radioButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radioButton2.setChecked(true);
                    radioButton.setChecked(false);
                }
            });
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radioButton2.setChecked(false);
                        radioButton.setChecked(true);
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image/*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(galleryIntent, "Choose your Avatar"), 1);

                    }
                });
                builder3.setPositiveButton("salva", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (radioButton.isChecked()) {
                            new UpdateAvatar(clientLocalStore.getUser(), 0).execute();
                        } else if (radioButton2.isChecked()) {
                            System.out.println("sono dentro al 2");
                            new UpdateAvatar(clientLocalStore.getUser(), 1).execute();
                        }else      System.out.println("sono dentro al 3");

                    }
                });
                builder3.setNegativeButton("annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder3.create().show();

                break;

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==1 && resultCode==RESULT_OK && data!=null) {
            Uri uri = data.getData();
            Log.i("PATH", uri.getEncodedPath());
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            avatar = cursor.getString(columnIndex);
            cursor.close();
            System.out.println("avatar path:" + avatar);
            File pathAvatar = new File(avatar);
            if (pathAvatar.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(pathAvatar.getAbsolutePath());
                String avatarString = BitMapToString(myBitmap);
                new UploadTask(avatarString,clientLocalStore.getUser().getEmail().toString()).execute();
                new UpdateAvatar(clientLocalStore.getUser(),2);
            }
        }
        else avatar = "cake.png";
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
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

    public class UpdateAvatar extends AsyncTask<Void, Void, Void> {

        private User user;
        private int avatar;

        public UpdateAvatar(User user, int avatar) {
            this.user = user;
            this.avatar = avatar;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println(avatar);
            User user = clientLocalStore.getUser();
            user.setAvatar(avatar);
            clientLocalStore.updateUser(user);

            String role = clientLocalStore.getUser().getRole();
            int avatar = clientLocalStore.getUser().getAvatar();
            String email = clientLocalStore.getUser().getEmail();
            String url = "http://njsao.pythonanywhere.com/static/"+email+".png";
            user.getDrawableAvatar(role, avatar, imageprofile, getApplicationContext(), url);

            System.out.println(clientLocalStore.getUser().getAvatar());
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://njsao.pythonanywhere.com/update_avatar");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail().toString()));
                nameValuePairs.add(new BasicNameValuePair("avatar", String.valueOf(avatar)));
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

    public class UpdateTask extends AsyncTask<Void, Void, Void> {

        private User user;
        private String notice;

        public UpdateTask(User user, String notice) {
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
                HttpPost httpPost = new HttpPost("http://njsao.pythonanywhere.com/update_user");

                List<NameValuePair> nameValuePairs = new ArrayList<>(9);
                nameValuePairs.add(new BasicNameValuePair("id", Integer.toString(user.getId_user())));

                nameValuePairs.add(new BasicNameValuePair("name", user.getName()));

                nameValuePairs.add(new BasicNameValuePair("surname", user.getSurname()));

                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));

                nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));

                nameValuePairs.add(new BasicNameValuePair("role", user.getRole()));

                nameValuePairs.add(new BasicNameValuePair("city", user.getCity()));

                nameValuePairs.add(new BasicNameValuePair("rate", Double.toString(user.getRate())));

                nameValuePairs.add(new BasicNameValuePair("description", user.getDescription()));

                nameValuePairs.add(new BasicNameValuePair("bday", user.getBday()));

                nameValuePairs.add(new BasicNameValuePair("avatar", Integer.toString(user.getAvatar())));

                nameValuePairs.add(new BasicNameValuePair("active", Integer.toString(user.isActive() ? 1 : 0)));

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

    public class UpdateStatusTask extends AsyncTask<Void, Void, Void> {
        private User user;

        public UpdateStatusTask(User user) {
            this.user = user;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            clientLocalStore.updateUser(user);
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://njsao.pythonanywhere.com/update_status");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("active", Integer.toString(user.isActive()?1:0)));
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
        private String notice;

        public NoticeTask(User user, String notice) {
            this.user = user;
            this.notice = notice;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://njsao.pythonanywhere.com/add_notice");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("user_email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("notice_text", notice.toString()));
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

    public class FeedBackTask extends AsyncTask<Void, Void, Void> {

        private User user;
        private int feedbackCount;
        private int followersCount;
        private int followedCount;
        private String noticeTxt;


        public FeedBackTask(User user) {
            this.user = user;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            feedback.setText(Integer.toString(feedbackCount));
            followers.setText(Integer.toString(followersCount));
            followed.setText(Integer.toString(followedCount));
            if (noticeTxt.equals(" ")){
                notice_tv.setText("inserisci un annuncio per aiutare i clienti a conoscere le tue offerte");
            }else {
                notice_tv.setText(noticeTxt);
            }

        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://njsao.pythonanywhere.com/get_counters");
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity);
                JSONObject jsonObject= new JSONObject(json);
                feedbackCount = jsonObject.getInt("feedbacks");
                followersCount = jsonObject.getInt("followers");
                followedCount = jsonObject.getInt("followed");
                noticeTxt = jsonObject.getString("notice");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class UploadTask extends AsyncTask<Void, Void, Void> {
        String imageString;
        String response;
        String name;

        public UploadTask(String imageString,String name) {
            this.imageString = imageString;
            this.name = name;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            try {
//                URL image= new URL("http://njsao.pythonanywhere.com/get_image/"+name);
//                Bitmap bmp = BitmapFactory.decodeStream(image.openConnection().getInputStream());
//                imageprofile.setImageBitmap(bmp);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://njsao.pythonanywhere.com/save_image");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("image", imageString));
                nameValuePairs.add(new BasicNameValuePair("email", name));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
