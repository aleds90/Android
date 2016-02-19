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
import android.widget.ImageView;
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
import org.json.JSONObject;

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
    ImageView avatar;

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
        notice_tv       = (TextView)findViewById(R.id.otherProfile_notice_tv);
        avatar          = (ImageView)findViewById(R.id.otherProfile_avatar_imgv);
        String role = target.getRole();
        int avatarInt = target.getAvatar();
        String email = target.getEmail();
        String url = "http://njsao.pythonanywhere.com/static/"+email+".png";

        target.getDrawableAvatar(role,avatarInt,avatar,getApplicationContext() ,url);
        System.out.println(avatarInt);

        //TEXTVIEW SETTINGS
        textViewName.setText(target.getName()+" "+target.getSurname());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
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
        textViewRate.setText(Double.toString(target.getRate())+"€");
        //STATUS TEXTVIEW
            if (target.isActive()){
            textViewStatus.setText(getResources().getText(R.string.available));
        }else {
            textViewStatus.setText(getResources().getText(R.string.not_available));
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
        int avatar  =bundle.getInt("avatar");
        User user = new User(id_user, name, cognome, email, "", bday, role, city, rate,
                active, description,avatar);
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

        MenuObject send = new MenuObject(getResources().getString(R.string.send_message));
        send.setResource(R.drawable.icn_1);

        like = new MenuObject();
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icn_2);
        like.setBitmap(b);

        addFav = new MenuObject();
        addFav.setResource(R.drawable.icn_4);

        MenuObject block = new MenuObject(getResources().getString(R.string.block_profile));
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
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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
        switch (position){
            case 1:
                Intent intent = new Intent(this, MessageActivity.class);
                intent.putExtra("userEmail", target.getEmail());
                intent.putExtra("userName", target.getName());
                startActivity(intent);
                break;
            case 2:
                new AddFeedbackTask(user, target).execute();
                break;
            case 3:
                new AddFollowTask(user, target).execute();
                break;
            case 4:
                Toast.makeText(this, getResources().getString(R.string.not_available), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
    }

    public class CheckFollowTask extends AsyncTask<Void, Void, Void> {
        User user;
        User target;
        boolean isFollowAvailable;
        boolean isFeedbackAvailable;
        int followersText;
        int feedbackText;
        String noticeText;

        public CheckFollowTask(User user, User target) {
            this.user = user;
            this.target = target;
        }
        @Override
        protected void onPostExecute (Void aVoid){
            super.onPostExecute(aVoid);
            if (!isFollowAvailable) {
                addFav.setTitle(getResources().getString(R.string.add_follow));

            } else {
                addFav.setTitle(getResources().getString(R.string.stop_follow));
            }
            if (isFeedbackAvailable) {
                like.setTitle(getResources().getString(R.string.add_feedback));
            } else {
                like.setTitle(getResources().getString(R.string.feedback_not_available));
            }
            feedback.setText(Integer.toString(feedbackText));
            followers.setText(Integer.toString(followersText));
            notice_tv.setText(noticeText);
            if (noticeText == null)
                notice_tv.setText(getResources().getString(R.string.not_offers));
        }
        @Override
        protected Void doInBackground (Void...params){
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://njsao.pythonanywhere.com/other_profile_init");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("user_email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("other_email", target.getEmail()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println(json);
                JSONObject jsonObject =  new JSONObject(json);
                isFollowAvailable = jsonObject.getBoolean("is_follow_available");
                isFeedbackAvailable = jsonObject.getBoolean("is_feedback_available");
                followersText = jsonObject.getInt("followers");
                feedbackText = jsonObject.getInt("feedback");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
            if (newText.equals("OK"))
                addFav.setTitle("Non seguire piu'");
            else addFav.setTitle("Segui Profilo");
        }

        @Override
        protected Void doInBackground (Void...params){
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://njsao.pythonanywhere.com/add_follow");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("user_email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("target_email", target.getEmail()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                newText = EntityUtils.toString(entity);
                System.out.println("follow:"+newText);
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
                    HttpPost httppost = new HttpPost("http://njsao.pythonanywhere.com/add_feedback");
                    List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                    nameValuePairs.add(new BasicNameValuePair("user_email", user.getEmail()));
                    nameValuePairs.add(new BasicNameValuePair("target_email", target.getEmail()));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    String newText = EntityUtils.toString(entity);
                    System.out.println("follow:"+newText);
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
