package com.example.alessandro.loginandroid.Tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ListView;

import com.example.alessandro.loginandroid.Activity.LoginActivity;
import com.example.alessandro.loginandroid.Activity.MainActivity;
import com.example.alessandro.loginandroid.Adapters.ListUser;
import com.example.alessandro.loginandroid.Entity.Client;
import com.example.alessandro.loginandroid.Entity.ClientLocalStore;
import com.example.alessandro.loginandroid.Entity.Message;
import com.example.alessandro.loginandroid.Entity.Response;
import com.example.alessandro.loginandroid.Entity.User;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class ToServerTasks {


    public static class LoginTask extends AsyncTask<String, Void, Void> {

        //attributi necessari per provare ad effettuare un login
        private Client client;
        private User user;
        private ClientLocalStore clientlocalstore;
        private Context context;

        //costruttore della classe Task
        public LoginTask(Client client, User user, ClientLocalStore clientlocalstore, Context context) {

            this.client = client;
            this.user = user;
            this.clientlocalstore = clientlocalstore;
            this.context = context;
        }

        /*
         * Lavoro che svolge in background la classe Task. Invia la richiesta ad un indirizzo e riceve una risposta da quest'ultimo
         */
        @Override
        protected Void doInBackground(String... params) {
            try {

                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://njsao.pythonanywhere.com/login");
                // Lista dei valori che mandiamo
                List<NameValuePair> nameValuePairs = new ArrayList<>(5);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("random_id", client.getRANDOM_ID()));
                nameValuePairs.add(new BasicNameValuePair("secret_id", client.getSECRET_ID()));
                nameValuePairs.add(new BasicNameValuePair("grant_types", client.getGRANT_TYPES()));
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
                //Mettere la lista nel post, cosi da poterla mandare
                // prima l aveva solo creata ma non settata come da mandare
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Settiamo l Header perché il sito si aspetta un header Parametro di tipo Authorization
                httppost.setHeader("Authorization", client.getRefreshToken());
                // con l execute, mandiamo il post
                HttpResponse response = httpclient.execute(httppost);
                // prendiamo la risposta del server e lo salviamo come stringa in "json"
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println(json);
                // Creiamo una classe di tipo Response grazie alla risposta json
                Response responseServer = new Gson().fromJson(json, Response.class);
                // Metodo per gestire la risposta ottenuta
                handleResponse(responseServer, clientlocalstore, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static void handleResponse(Response responseServer, ClientLocalStore clientLocalStore, Context context) {
        switch (responseServer.getType()) {
            // response:2, login effettuato. Inseriamo i Token ottenuti, i dati dell'utente e del client nel nostro db e veniamo inviati nella main activity
            case "2":
                User user = responseServer.getUser();
                Client client = new Client(responseServer.getAccess_Token(), responseServer.getRefresh_Token(), "");
                clientLocalStore.storeClientData(client, user);
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                break;
            //response:3, il login non e' andato a buon fine quindi rimane sulla login activity per ritentare
            case "3":
                break;
            case "4":
                User user1 = clientLocalStore.getUser();
                System.out.println(user1.getName());
                Client client1 = new Client(responseServer.getAccess_Token(), responseServer.getRefresh_Token(), "");
                clientLocalStore.storeClientData(client1, user1);
                break;
            //login tramite refresh non andato a buon fine. Veniamo reinderizzati nella pagina di login
            case "401":
                Intent intent1 = new Intent(context, LoginActivity.class);
                context.startActivity(intent1);
                break;
        }
    }

    public static class CheckFollowTask extends AsyncTask<Void, Void, Void> {
        User user;
        User target;
        boolean isFollower;
        Button btnFollowOtherProfile;


        public CheckFollowTask(User user, User target, Button btnFollowOtherProfile) {
            this.user = user;
            this.target = target;
            this.btnFollowOtherProfile = btnFollowOtherProfile;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isFollower) {
                btnFollowOtherProfile.setText("Unfollow");
            } else {
                btnFollowOtherProfile.setText("Follow");
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/checkFollow");
                // Lista dei valori che mandiamo
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("emailUser", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("emailTarget", target.getEmail()));

                //Mettere la lista nel post, cosi da poterla mandare
                // prima l aveva solo creata ma non settata come da mandare
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                // con l execute, mandiamo il post
                HttpResponse response = httpclient.execute(httppost);
                // prendiamo la risposta del server e lo salviamo come stringa in "json"
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

    public static class AddFollowTask extends AsyncTask<Void, Void, Void> {
        User user;
        User target;
        String newText;
        Button btnFollowOtherProfile;

        public AddFollowTask(User user, User target, Button btnFollowOtherProfile) {
            this.user = user;
            this.target = target;
            this.btnFollowOtherProfile = btnFollowOtherProfile;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            btnFollowOtherProfile.setText(newText);

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/addFollow");
                // Lista dei valori che mandiamo
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("emailUser", user.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("emailTarget", target.getEmail()));

                //Mettere la lista nel post, cosi da poterla mandare
                // prima l aveva solo creata ma non settata come da mandare
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                // con l execute, mandiamo il post
                HttpResponse response = httpclient.execute(httppost);
                // prendiamo la risposta del server e lo salviamo come stringa in "json"
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

    public static class MainTask extends AsyncTask<String, Void, Void> {

        // attributi necessari per il login  tramite refresh
        private Client client;
        private ClientLocalStore clientLocalStore;
        private Context context;

        //costruttore
        public MainTask(Client client, ClientLocalStore clientLocalStore, Context context) {
            this.client = client;
            this.clientLocalStore = clientLocalStore;
            this.context = context;
        }


        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post
                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/authorization");
                // Lista dei valori che mandiamo
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("random_id", client.getRANDOM_ID()));
                nameValuePairs.add(new BasicNameValuePair("secret_id", client.getSECRET_ID()));
                nameValuePairs.add(new BasicNameValuePair("grant_types", client.getGRANT_TYPES()));
                //Mettere la lista nel post, cosi da poterla mandare
                // prima l aveva solo creata ma non settata come da mandare
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Settiamo l Header perché il sito si aspetta un header Parametro di tipo Authorization
                httppost.setHeader("Authorization", client.getRefreshToken());
                // con l execute, mandiamo il post
                HttpResponse response = httpclient.execute(httppost);
                // prendiamo la risposta del server e lo salviamo come stringa in "json"
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println(json);
                // Creiamo una classe di tipo Response grazie alla risposta json
                Response responseServer = new Gson().fromJson(json, Response.class);
                // Metodo per gestire la risposta ottenuta
                handleResponse(responseServer, clientLocalStore, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class UserListTask extends AsyncTask<Void, Void, Void> {
        private User user;
        private JSONArray usersArray;
        private ArrayList<User> users;
        private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
        private ListUser adapter;
        private ListView listViewUSERLIST;
        Context context;

        public UserListTask(User user, JSONArray usersArray, ArrayList<User> users, WaveSwipeRefreshLayout mWaveSwipeRefreshLayout, ListUser adapter, ListView listViewUSERLIST, Context context) {
            this.user = user;
            this.usersArray = usersArray;
            this.users = users;
            this.mWaveSwipeRefreshLayout = mWaveSwipeRefreshLayout;
            this.adapter = adapter;
            this.listViewUSERLIST = listViewUSERLIST;
            this.context = context;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            users = new ArrayList<>();
            for (int i = 0; i < usersArray.length(); i++) {
                User user = null;
                try {
                    user = new Gson().fromJson(usersArray.get(i).toString(), User.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                users.add(user);
            }

            setAdapter(adapter, users, listViewUSERLIST, context);
            mWaveSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post

                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/users");
                // HttpGet httpGet = new HttpGet("http://10.0.2.2:4567/users");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                usersArray = new JSONArray(json);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static void setAdapter( ListUser adapter, ArrayList<User> users, ListView listViewUSERLIST, Context context) {
        adapter = new ListUser(context, users);
        listViewUSERLIST.setAdapter(adapter);
    }

    public class GetConversation extends AsyncTask<Void,Void,Void> {
        private String userEmail;
        private String myEmail;
        private JSONArray usersArray;
        private ArrayList<Message> messages;

        public GetConversation(String userEmail,String myEmail){
            this.userEmail=userEmail;
            this.myEmail=myEmail;}

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            messages = new ArrayList<>();
            for (int i = 0; i < usersArray.length(); i++) {
                Message message = null;
                try {
                    message = new Gson().fromJson(usersArray.get(i).toString(), Message.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                messages.add(message);
                //setAdapter();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/getConversation");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user_mail", userEmail));
                nameValuePairs.add(new BasicNameValuePair("my_mail", myEmail));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity =response.getEntity();
                String json = EntityUtils.toString(entity);
                System.out.println(json);
                usersArray = new JSONArray(json);
                System.out.println(json);


            }catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class DeleteTask extends AsyncTask<Void, Void, Void> {
        private User user;

        public DeleteTask(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.0.2.2:4567/delete");

                List<NameValuePair> nameValuePairs = new ArrayList<>(1);

                nameValuePairs.add(new BasicNameValuePair("id_user", Integer.toString(user.getId_user())));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);

                System.out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class FilteredUserListTask extends AsyncTask<Void, Void, Void> {

        private User user;
        private ArrayList<User> users;
        JSONArray usersArray;


        public FilteredUserListTask(User user, ArrayList<User> users) {

            this.user = user;
            this.users = users;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int i = 0; i < usersArray.length(); i++) {
                User user = null;
                try {
                    user = new Gson().fromJson(usersArray.get(i).toString(), User.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                users.add(user);
            }


        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                // sito a cui fare il post

                HttpPost httppost = new HttpPost("http://10.0.2.2:4567/getFiltered");
                // HttpGet httpGet = new HttpGet("http://10.0.2.2:4567/users");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                // Valori:
                nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
                nameValuePairs.add(new BasicNameValuePair("city", user.getCity()));
                nameValuePairs.add(new BasicNameValuePair("role", user.getRole()));
                nameValuePairs.add(new BasicNameValuePair("rate", Double.toString(user.getRate())));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                usersArray = new JSONArray(json);





            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
