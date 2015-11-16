package com.example.alessandro.loginandroid.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toolbar;

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
import java.util.ArrayList;
import java.util.List;

public class ResearchActivity extends Activity implements CompoundButton.OnCheckedChangeListener, TextWatcher {
    ListView userList;
    ArrayList<User> users;
    EditText edName, edRole, edRate, edResume;
    Button bStartSearch;
    Switch swName, swRole, swRate;
    UserArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_research);

        userList = (ListView) findViewById(R.id.lvResearch);
        edResume = (EditText) findViewById(R.id.etResume);
        edName = (EditText)findViewById(R.id.edName);
        edRate = (EditText)findViewById(R.id.edRate);
        edRole = (EditText)findViewById(R.id.edRole);
        swName = (Switch) findViewById(R.id.switchName);
        swRate = (Switch) findViewById(R.id.switchRate);
        swRole = (Switch) findViewById(R.id.switchRole);

        edName.addTextChangedListener(this);
        edRate.addTextChangedListener(this);
        edRole.addTextChangedListener(this);

        swName.setOnCheckedChangeListener(this);
        swRate.setOnCheckedChangeListener(this);
        swRole.setOnCheckedChangeListener(this);

        bStartSearch = (Button)findViewById(R.id.bStartSearch);


        //creo collegamento tra le componenti la classe e activity_main.xml
        // definiamo e settiamo la lista di utenti che riempira la nostra listview


        bStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users = new ArrayList<>();
                User user = getUser();
                new UserByAttributeTask(user).execute();

            }
        });
    }

    private User getUser() {
        User user = new User();
        if (swName.isChecked()) {
            user.setName(edName.getText().toString());
        }
        if (swRole.isChecked()) {
            user.setRole(edRole.getText().toString());
        }
        if (swRate.isChecked()) {
            user.setRate(Double.parseDouble(edRate.getText().toString()));
        }
        return user;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switchName:
                if (b) {
                    edName.setEnabled(true);
                } else {
                    edName.setEnabled(false);
                }
                break;
            case R.id.switchRate:
                if (b) {
                    edRate.setEnabled(true);
                } else {
                    edRate.setEnabled(false);
                }
                break;
            case R.id.switchRole:
                if (b) {
                    edRole.setEnabled(true);
                } else {
                    edRole.setEnabled(false);
                }
                break;
            default:
                setResume();
        }
        setResume();

    }

    private void setResume() {
        String resume = " ";
        if (swName.isChecked()) {
            resume = resume + edName.getText().toString();
        }
        if (swRole.isChecked()) {
            resume = resume + " | " + edRole.getText().toString();
        }
        if (swRate.isChecked()) {
            resume = resume + " | " + edRate.getText().toString();
        }
        edResume.setText(resume);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        setResume();
    }

    public class UserByAttributeTask extends AsyncTask<Void, Void, Void> {
        private User researchUser;
        public UserByAttributeTask(User researchUser){
            this.researchUser = researchUser;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new UserArrayAdapter(getApplicationContext(), users);
            adapter.notifyDataSetChanged();
            userList.setAdapter(adapter);
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
                Log.v("EEEEEEEEE", json);

                for (int i = 0; i < usersArray.length(); i++) {
                    User user = new Gson().fromJson(usersArray.get(i).toString(), User.class);
                    users.add(user);
                    Log.v("EEEEEEEEE", "" + users.size());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * @param menu gli passiamo sto menu che sará la nostra navigaton_bar(vedi res->menu)
     * @return ritorna sempre true
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
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
            case R.id.Profil:
                intent = new Intent(this, OtherProfileActivity.class);
                break;
        }
        startActivity(intent);
        return true;
    }

}
