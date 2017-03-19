package com.forme.yeo.pocketmoney;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main2Activity extends AppCompatActivity {

    private Spinner spnType;
    private EditText etDate, etDescribe, etAmount;
    private Button btnOk,btnCancel;
    DBHandler dbHandler;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String type[] = {"Breakfast", "Lunch", "Dinner", "Groceries", "Motorcycle", "Out Going", "Supper", "Other"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Calendar calendar = Calendar.getInstance();
        dbHandler = new DBHandler(this);
        etDate = (EditText)findViewById(R.id.etDate);
        etAmount = (EditText)findViewById(R.id.etAmount);
        etDescribe = (EditText)findViewById(R.id.etDescribe);
        btnOk = (Button) findViewById(R.id.btnNewOk);
        btnCancel = (Button) findViewById(R.id.btnNewCancel);
        etDate.setText(sdf.format(calendar.getTime()));
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(Main2Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        etDate.setText(sdf.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        spnType = (Spinner) findViewById(R.id.spnType);
        ArrayAdapter adapter = new ArrayAdapter(Main2Activity.this, R.layout.support_simple_spinner_dropdown_item, type);
        spnType.setAdapter(adapter);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(etAmount.getText().toString());
                String date = etDate.getText().toString();
                String describe = etDescribe.getText().toString();
                String type = spnType.getSelectedItem().toString();
                SingleItem singleItem = new SingleItem(date, describe, type, amount);
                dbHandler.insertDetail(singleItem.toHashMap());
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
