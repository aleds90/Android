package com.example.alessandro.loginandroid.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.alessandro.loginandroid.R;

public class MyProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
    }

    /**
     * @param menu gli passiamo sto menu che sará la nostra navigaton_bar(vedi res->menu)
     * @return ritorna sempre true
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.actionbar2);
        tb.inflateMenu(R.menu.navigation_bar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    /**
     * @param item un po come View v per OnClickListerner solo che per gli items del menu
     * @return ritorna sempre true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.Home:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.Search:
                intent = new Intent(this, ResearchActivity.class);
                break;
            case R.id.Follow:
                intent = new Intent(this, FollowActivity.class);
                break;
            case R.id.Profil:
                intent = new Intent(this, MyProfileActivity.class);
                break;
        }
        startActivity(intent);
        return true;
    }
}
