package com.example.alessandro.loginandroid.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Notice;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.google.gson.Gson;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

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
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OtherProfileActivity extends AppCompatActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener{

    final String NEW_FORMAT = "dd-MM-yyyy";
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    TextView textViewName,textViewBday,textViewRole,textViewCity,textViewRate,textViewStatus,notice_tv,feedback,followers,description;
    User target;
    MenuObject addFav;
    ClientLocalStore clientLocalStore;
    MenuObject like;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_profile_activity);
        Bundle bundle = getIntent().getExtras();
        target = getUserBybundle(bundle);
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();
        setAllTextView();
        clientLocalStore = new ClientLocalStore(this);
        user = clientLocalStore.getUser();
        new CheckFollowTask(user,target).execute();
        new CheckFeedbackTask(user,target).execute();
        new FeedBackTask(target).execute();
        new NoticeTask(target).execute();
    }

    public void setAllTextView(){

        textViewName    = (TextView)findViewById(R.id.otherProfile_name_textview);
        textViewBday    = (TextView)findViewById(R.id.otherProfile_bday_textview);
        textViewRole    = (TextView)findViewById(R.id.otherProfile_role_textview);
        textViewCity    = (TextView)findViewById(R.id.otherProfile_city_textview);
        textViewRate    = (TextView)findViewById(R.id.otherProfile_rate_textview);
        textViewStatus  = (TextView)findViewById(R.id.otherProfile_status_textview);
        followers       = (TextView)findViewById(R.id.otherProfile_followers_textview);
        feedback        = (TextView)findViewById(R.id.otherProfile_feedback_tv);
        description     = (TextView)findViewById(R.id.otherProfile_description_tv);
        notice_tv          = (TextView)findViewById(R.id.otherProfile_notice_tv);

        //TEXTVIEW SETTINGS
        textViewName.setText(target.getName()+" "+target.getSurname());
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.ITALY);
        try {
            Date date = format.parse(target.getBday());
            format.applyPattern(NEW_FORMAT);
            String newDate=format.format(date);
            System.out.println(newDate);
            textViewBday.setText(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        textViewRole.setText(target.getRole());
        textViewCity.setText(target.getCity());
        //RATE TEXTVIEW
        if (target.getRate()<=50) {
            textViewRate.setText("(bassa)");
            textViewRate.setTextColor(Color.parseColor("#ff00ff00"));
        }else if(target.getRate()>50&&target.getRate()<100){
            textViewRate.setText("(media)");
            textViewRate.setTextColor(Color.parseColor("#ff0000ff"));
        }else{
            textViewRate.setText("(alta)");
            textViewRate.setTextColor(Color.parseColor("#ffff0000"));
        }
        //STATUS TEXTVIEW
        System.out.println(target.isActive()+" ciao");
        if (target.isActive()){
            textViewStatus.setText("Disponibile");
            textViewStatus.setTextColor(Color.parseColor("#ff00ff00"));
        }else {
            textViewStatus.setText("Non Disponibile");
            textViewStatus.setTextColor(Color.parseColor("#ffff0000"));
        }
        description.setText(target.getDescription());

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
        boolean active = bundle.getBoolean("status");
        String description = bundle.getString("description");
        System.out.println("descrizione ");

        User user = new User(id_user, name, cognome, email, "", bday, role, city, rate,
                active, description);
        return user;
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject send = new MenuObject("Invia messaggio");
        send.setResource(R.drawable.icn_1);

        like = new MenuObject("Lascia feedback");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icn_2);
        like.setBitmap(b);

        addFav = new MenuObject("Segui profilo");
        addFav.setResource(R.drawable.icn_4);

        MenuObject block = new MenuObject("Blocca profilo");
        block.setResource(R.drawable.icn_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);

        menuObjects.add(addFav);
        menuObjects.add(block);
        return menuObjects;
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolBarTextView.setText(target.getName());
    }

    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else{
            finish();
        }
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
        switch (position){
            case 1:
                Intent intent = new Intent(this, MessageActivity.class);
                intent.putExtra("userEmail", target.getEmail());
                startActivity(intent);
                break;
            case 2:
                new AddFeedbackTask(user, target).execute();
                break;
            case 3:
                new AddFollowTask(user, target).execute();
                break;
            case 4:
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
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
                addFav.setTitle("Non seguire piu'");
            } else {
                addFav.setTitle("Segui profilo");
            }
        }
        @Override
        protected Void doInBackground (Void...params){
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/checkFollow");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("emailUser", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("emailTarget", target.getEmail()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
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


    public class CheckFeedbackTask extends AsyncTask<Void, Void, Void> {
        User user;
        User target;
        boolean isFeedbackPossible;


        public CheckFeedbackTask(User user, User target) {
            this.user = user;
            this.target = target;
        }
        @Override
        protected void onPostExecute (Void aVoid){
            super.onPostExecute(aVoid);
            if (isFeedbackPossible) {
                like.setTitle("Lascia un feedback");
            } else {
                like.setTitle("No feedback");
            }
        }
        @Override
        protected Void doInBackground (Void...params){
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/checkFeedback");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("user_email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("target_email", target.getEmail()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println("RISPOSTA >>>>>>>> " +json);
                isFeedbackPossible = json.equals("true");


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
            if (newText.equals("FOLLOW"))
                addFav.setTitle("Segui Profilo");
            else addFav.setTitle("Non seguire piu'");
        }

        @Override
        protected Void doInBackground (Void...params){
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/addFollow");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("emailUser", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("emailTarget", target.getEmail()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
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


    public class AddFeedbackTask extends AsyncTask<Void, Void, Void>{

        User user;
        User target;

        public AddFeedbackTask(User user, User target) {
                this.user = user;
                this.target = target;
            }

        @Override
        protected void onPostExecute (Void aVoid){
                super.onPostExecute(aVoid);

            }

        @Override
        protected Void doInBackground(Void...params){
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://10.0.2.2:4567/addFeedback");
                    List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                    nameValuePairs.add(new BasicNameValuePair("user_email", user.getEmail()));
                    nameValuePairs.add(new BasicNameValuePair("target_email", target.getEmail()));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
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
