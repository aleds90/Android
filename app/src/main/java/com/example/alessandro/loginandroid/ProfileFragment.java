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
    private User user;

    public static ProfileFragment newInstance(User user){
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("User", user.getSurname());
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
        buttonGoFrag = (Button)view.findViewById(R.id.buttonGoFrag);
        textViewCityFrag.setText(getArguments().getString("User"));

        buttonGoFrag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });


        return view;
    }

}
