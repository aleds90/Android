package com.example.alessandro.loginandroid.Activity;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.alessandro.loginandroid.R;

import java.util.List;

public class MessageActivity extends Activity {

    private ListView messageListView;
    private EditText typeMessageEditText;
    private ImageView sendMessageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageListView = (ListView)findViewById(R.id.messageListView);
        typeMessageEditText = (EditText)findViewById(R.id.typeMessageEditText);
        sendMessageButton = (ImageView)findViewById(R.id.sendMessageButton);

    }

}
