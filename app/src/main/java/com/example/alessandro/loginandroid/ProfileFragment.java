package com.example.alessandro.loginandroid;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Entity.User;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {
    View view;
    ImageView imageViewFrag;
    TextView textViewNameFrag, textViewRoleFrag, textViewCityFrag, textViewRateFrag;
    Button buttonGoFrag;

    public static ProfileFragment newInstance(String username){
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("User", username);
        profileFragment.setArguments(args);
        return profileFragment;
    }
    public ProfileFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blank, container, false);
        imageViewFrag = (ImageView) view.findViewById(R.id.imageViewFrag);
        textViewNameFrag = (TextView) view.findViewById(R.id.textViewNameFrag);
        textViewRoleFrag = (TextView) view.findViewById(R.id.textViewRoleFrag);
        textViewCityFrag = (TextView) view.findViewById(R.id.textViewCityFrag);
        textViewRateFrag = (TextView) view.findViewById(R.id.textViewRateFrag);
        String username = this.getArguments().getString("username");
        String role = this.getArguments().getString("role");

        buttonGoFrag = (Button)view.findViewById(R.id.buttonGoFrag);
        textViewNameFrag.setText(username);
        textViewRoleFrag.setText(role);

        buttonGoFrag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });


        return view;
    }

    public void setFields(User user){
        textViewNameFrag.setText(user.getName());
        textViewRoleFrag.setText(user.getRole());
        textViewCityFrag.setText(user.getCity());
        textViewRateFrag.setText(Double.toString(user.getRate()));
    }

}
