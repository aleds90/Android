package com.example.alessandro.loginandroid.Test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.alessandro.loginandroid.R;

public class testFollowActivity extends Activity implements View.OnClickListener {
    ImageButton buttonHOME, buttonSEARCH, buttonFOLLOW, buttonPROFILE;

    private Button followContactButton;
    private Button followMessageButton;

    private ListView followListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_follow);

        buttonHOME = (ImageButton) findViewById(R.id.buttonHOME);
        buttonSEARCH = (ImageButton) findViewById(R.id.buttonSEARCH);
        buttonFOLLOW = (ImageButton) findViewById(R.id.buttonFOLLOW);
        buttonPROFILE = (ImageButton) findViewById(R.id.buttonPROFILE);

        buttonHOME.setOnClickListener(this);
        buttonSEARCH.setOnClickListener(this);
        buttonFOLLOW.setOnClickListener(this);
        buttonPROFILE.setOnClickListener(this);

        followContactButton = (Button) findViewById(R.id.followContactButton);
        followMessageButton = (Button) findViewById(R.id.followMessageButton);

        followContactButton.setOnClickListener(this);
        followMessageButton.setOnClickListener(this);

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
            case R.id.followContactButton:
                break;
            case R.id.followMessageButton:
                break;
        }

    }
}