package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.google.gson.Gson;

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
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

public class ResearchActivity extends Activity {
    ListView userList;
    ArrayList<User> users;
    EditText edName, edRole, edRate;
    Button bStartSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_research);

        userList = (ListView) findViewById(R.id.lvResearch);
        edName = (EditText)findViewById(R.id.edName);
        edRate = (EditText)findViewById(R.id.edRate);
        edRole = (EditText)findViewById(R.id.edRole);
        bStartSearch = (Button)findViewById(R.id.bStartSearch);
        //creo collegamento tra le componenti la classe e activity_main.xml
        // definiamo e settiamo la lista di utenti che riempira la nostra listview


        bStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users = new ArrayList<>();
                User user = new User();
                user.setName(checkEdName(edName.getText().toString()));
                user.setRole(checkEdRole(edRole.getText().toString()));
                user.setRate(Double.parseDouble(checkEdRate(edRate.getText().toString())));
                new UserByAttributeTask(user).execute();
                UserArrayAdapter adapter = new UserArrayAdapter(getApplicationContext(), users);
                userList.setAdapter(adapter);

            }
        });
    }

    public class UserByAttributeTask extends AsyncTask<Void, Void, Void> {
        private User researchUser;
        public UserByAttributeTask(User researchUser){
            this.researchUser = researchUser;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/getFiltered");

                List<NameValuePair> nameValuePairs = new ArrayList<>(3);

                System.out.println(researchUser.getName());
                System.out.println( Double.toString(researchUser.getRate()));
                System.out.println(researchUser.getRole());

                nameValuePairs.add(new BasicNameValuePair("name", researchUser.getName()));
                nameValuePairs.add(new BasicNameValuePair("rate", Double.toString(researchUser.getRate())));
                nameValuePairs.add(new BasicNameValuePair("role", researchUser.getRole()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                JSONArray usersArray = new JSONArray(json);
                System.out.println(json);

                for (int i = 0; i < usersArray.length(); i++) {
                    User user = new Gson().fromJson(usersArray.get(i).toString(), User.class);
                    users.add(user);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String checkEdName (String name){
        if(name.equals("")){
            return "";
        }else return name;
    }

    public String checkEdRate(String rate){
        if (rate.equals("")){
            return "999999";
        }else return rate;
    }

    public String checkEdRole (String role){
        if(role.equals("")){
            return "";
        }else return role;
    }

}
