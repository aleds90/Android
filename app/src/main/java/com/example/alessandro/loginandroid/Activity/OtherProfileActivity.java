package com.example.alessandro.loginandroid.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

/**
 * Visulizza i dettagli di un profilo
 */
public class OtherProfileActivity extends Activity {

    TextView tvNome, tvCognome, tvEmail, tvRuolo, tvIndirizzo, tvBday, tvTariffa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        tvNome = (TextView) findViewById(R.id.tvNome);
        tvCognome = (TextView) findViewById(R.id.tvCognome);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvRuolo = (TextView) findViewById(R.id.tvRuolo);
        tvIndirizzo = (TextView) findViewById(R.id.tvIndirizzo);
        tvBday = (TextView) findViewById(R.id.tvBday);
        tvTariffa = (TextView) findViewById(R.id.tvRate);

        Bundle bundle = getIntent().getExtras();
        User user = getUserBybundle(bundle);

        tvNome.setText("Nome: " + user.getName());
        tvCognome.setText("Cognome: " + user.getSurname());
        tvEmail.setText("Email: " + user.getEmail());
        tvRuolo.setText(user.getRole());
        tvIndirizzo.setText("Zona: " + user.getCity());
        tvBday.setText("nato/a il: " + user.getBday());
        tvTariffa.setText("tariffa: " + user.getRate() + "/h");
    }

    private User getUserBybundle(Bundle bundle) {
        String name = bundle.getString("name");
        String cognome = bundle.getString("cognome");
        String city = bundle.getString("city");
        String email = bundle.getString("email");
        String bday = bundle.getString("bday");
        String role = bundle.getString("role");
        Double rate = bundle.getDouble("rate");
        return new User(name, cognome, email, "", bday, role, city, rate);
    }
}
