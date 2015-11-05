package com.example.alessandro.loginandroid;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class Test extends Activity implements View.OnClickListener {

    EditText etData_di_Nascita, etTariffa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpage1);

        /*etData_di_Nascita = (EditText)findViewById(R.id.etData_di_nascita);
        etTariffa = (EditText)findViewById(R.id.etTariffa);

        etData_di_Nascita.setOnClickListener(this);
        etTariffa.setOnClickListener(this);*/
    }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()){

            case R.id.etData_di_nascita:
                set_Data_di_nascita();
                break;
            case  R.id.etTariffa:
                set_Tariffa();


        }*/
    }

    private void set_Tariffa() {

    }

    private void set_Data_di_nascita() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //etData_di_Nascita.setText("Data di nascita: " + dayOfMonth + "-" + monthOfYear + "-" + year);
            }
        },mYear, mMonth, mDay);
        datePickerDialog.setTitle("Scegli data di nascita");
        datePickerDialog.show();
    }
}
