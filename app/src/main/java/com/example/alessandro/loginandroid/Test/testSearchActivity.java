package com.example.alessandro.loginandroid.Test;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.alessandro.loginandroid.R;

public class testSearchActivity extends Activity {

    private AutoCompleteTextView searchRoleAutoComplete;
    String[] roles = {"personal trainer", "pensionato", "cane", "gatto","paramedito","perdio","provaprova"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_search);

        searchRoleAutoComplete = (AutoCompleteTextView)findViewById(R.id.searchRoleAutoComplete);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_dropdown_item_1line, roles);
        searchRoleAutoComplete.setAdapter(adapter);
        searchRoleAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                String selected = (String) adapter.getItemAtPosition(pos);
                Toast.makeText(
                        getApplicationContext(),
                        "hai selezionato " + selected,
                        Toast.LENGTH_LONG
                ).show();
            }
        });

    }

}
