package com.example.alessandro.loginandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;

import java.util.ArrayList;


/**
 * Ogni lista ha bisogno di un ArrayAdapter, di solito funzionano con le String ma noi vogliamo
 * passargli un utente per questo ci creiamo un nostro UserAdapter
 * Created by sjnao on 11/13/15.
 */
public class ListUser extends ArrayAdapter<User> {

    private final Context context;
    private final ArrayList<User> users;

    /**
     * Costruttore
     * @param context per capire a che Activity/Listview si riferisce
     * @param users lista di utenti che verrá visualizzata
     */
    public ListUser(Context context, ArrayList<User> users) {
        super(context, R.layout.list_row, users);
        this.context = context;
        this.users = users;
    }

    /**
     * @param position questo parametero ci dice che posizione ha l elemento delle lista preso in considerazione
     * @param convertView idk sta li mai usato ma a quanto pare ci deve essere
     * @param parent vedi sopra
     * @return il return é il layout user_layout riempito da questi utenti
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // ci riferiamo a  user_layout come una riga/item di questa listView
        View rowView = inflater.inflate(R.layout.list_row, parent, false);
        // definizione delle componenti del layout(item)
        ImageView imageView= (ImageView)rowView.findViewById(R.id.gotoprofileButton);
        TextView textView = (TextView) rowView.findViewById(R.id.listNameTextView);
        TextView textView1 = (TextView) rowView.findViewById(R.id.listSurnameTextView);
        TextView textView2 = (TextView) rowView.findViewById(R.id.listRoleTextView);
        TextView textView3 = (TextView) rowView.findViewById(R.id.listCityTextView);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/rspu.ttf");


        //ImageButton button = (ImageButton) rowView.findViewById(R.id.bProfilo);
        // definiamo utente: ovvero il primo item(position=1) sara il primo utente nella lista users
        final User currentUser = users.get(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //andiamo a vederci nel dettagli l item(utent) clickato

                Intent intent = new Intent(context, OtherProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                passUserByIntent(intent, currentUser);
                context.startActivity(intent);
            }
        });
        //Per ora l utente é  visualizzato come Nome - Ruolo
        textView.setText(currentUser.getName());
        textView1.setText(currentUser.getSurname());
        textView2.setText(currentUser.getRole());
        textView3.setText(currentUser.getCity());
        return rowView;
    }

    /**
     * @param intent passiamo l intent dove mettere l utente
     * @param user per settare i vari Extras
     */
    private void passUserByIntent(Intent intent, User user) {
        intent.putExtra("name", user.getName());
        intent.putExtra("cognome", user.getSurname());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("city", user.getCity());
        intent.putExtra("role", user.getRole());
        intent.putExtra("bday", user.getBday());
        intent.putExtra("rate", user.getRate());
    }
}