package com.example.alessandro.loginandroid.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class testOtherProfileActivity extends AppCompatActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener{

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    TextView textViewName,textViewBday,textViewRole,textViewCity,textViewRate,textViewStatus;
    User target;
    MenuObject addFav;
    ClientLocalStore clientLocalStore;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_other_profile);
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
        //TEXTVIEW SETTINGS
        textViewName.setText(target.getName()+" "+target.getSurname());
        textViewBday.setText(target.getBday());
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
        if (textViewStatus.getText().equals("(disponibile)"))
            textViewStatus.setTextColor(Color.parseColor("#ff00ff00"));
        else textViewStatus.setTextColor(Color.parseColor("#ffff0000"));
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

        MenuObject like = new MenuObject("Lascia feedback");
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


}
