package com.example.alessandro.loginandroid;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Activity.OtherProfileActivity;
import com.example.alessandro.loginandroid.Entity.User;

public class ProfileFragment extends Fragment {
    View view;
    ImageView imageViewFrag;
    TextView textViewNameFrag, textViewRoleFrag, textViewCityFrag, textViewRateFrag;
    Button buttonGoFrag;


    public ProfileFragment(){}


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blank, container, false);
        imageViewFrag = (ImageView) view.findViewById(R.id.imageViewFrag);
        textViewNameFrag = (TextView) view.findViewById(R.id.textViewNameFrag);
        textViewRoleFrag = (TextView) view.findViewById(R.id.textViewRoleFrag);
        textViewCityFrag = (TextView) view.findViewById(R.id.textViewCityFrag);
        textViewRateFrag = (TextView) view.findViewById(R.id.textViewRateFrag);
        final User user = getUserFromActivity();


        buttonGoFrag = (Button)view.findViewById(R.id.buttonGoFrag);
        textViewNameFrag.setText(user.getName() + " " + user.getSurname());
        textViewRoleFrag.setText(user.getRole());
        textViewCityFrag.setText(user.getCity());
        textViewRateFrag.setText("      " + user.getRate() + "€/h");

        buttonGoFrag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), OtherProfileActivity.class);
                passUserByIntent(intent, user);
                startActivity(intent);
            }
        });


        return view;
    }

    private void passUserByIntent(Intent intent, User user) {
        intent.putExtra("name", user.getName());
        intent.putExtra("cognome", user.getSurname());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("city", user.getCity());
        intent.putExtra("role", user.getRole());
        intent.putExtra("bday", user.getBday());
        intent.putExtra("rate", user.getRate());
    }

    /**
     * @return User passato dal Activity
     */
    private User getUserFromActivity() {
        String name = this.getArguments().getString("name");
        String cognome = this.getArguments().getString("cognome");
        String city = this.getArguments().getString("city");
        String email = this.getArguments().getString("email");
        String bday = this.getArguments().getString("bday");
        String role = this.getArguments().getString("role");
        Double rate = this.getArguments().getDouble("rate");
        return new User(name, cognome, email, "", bday, role, city, rate);
    }


}
