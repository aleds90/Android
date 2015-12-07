package com.example.alessandro.loginandroid.Test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.alessandro.loginandroid.R;

public class testFollowActivity extends Activity implements View.OnClickListener {
    private Button followToHomeButton;
    private Button followToSearchButton;
    private Button followToFollowButton;
    private Button followToProfileButton;

    private Button followContactButton;
    private Button followMessageButton;

    private ListView followListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_follow);

        followToHomeButton = (Button) findViewById(R.id.followToHomeButton);
        followToSearchButton = (Button) findViewById(R.id.followToSearchButton);
        followToFollowButton = (Button) findViewById(R.id.followToFollowButton);
        followToProfileButton = (Button) findViewById(R.id.followToProfileButton);

        followToHomeButton.setOnClickListener(this);
        followToSearchButton.setOnClickListener(this);
        followToFollowButton.setOnClickListener(this);
        followToProfileButton.setOnClickListener(this);

        followContactButton = (Button) findViewById(R.id.followContactButton);
        followMessageButton = (Button) findViewById(R.id.followMessageButton);

        followContactButton.setOnClickListener(this);
        followMessageButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.followToHomeButton:
                Intent intent1 = new Intent(this, testMainActivity.class);
                startActivity(intent1);
                break;
            case R.id.followToSearchButton:
                Intent intent2 = new Intent(this, testSearchActivity.class);
                startActivity(intent2);
                break;
            case R.id.followToFollowButton:
                Intent intent3 = new Intent(this, testFollowActivity.class);
                startActivity(intent3);
                break;
            case R.id.followToProfileButton:
                Intent intent4 = new Intent(this, testProfileActivity.class);
                startActivity(intent4);
                break;
            case R.id.followContactButton:
                break;
            case R.id.followMessageButton:
                break;
        }

    }
}