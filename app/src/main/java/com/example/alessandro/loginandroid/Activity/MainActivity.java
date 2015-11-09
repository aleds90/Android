package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.alessandro.loginandroid.Entity.Client;
import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;


public class MainActivity extends Activity {
    private Button bSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bSend = (Button) findViewById(R.id.bSend);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setEmail("test");
                user.setPassword("test");
                Client client = new Client(user, "e8liqmaso3ukav3m3e8c0i0t9v", "e8liqmaso3ukav3m3e8c0i0t9v", "Refresh");
                client.login();
            }
        });
    }
}