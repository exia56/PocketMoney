package com.forme.yeo.pocketmoney;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main2Activity extends AppCompatActivity {

    private Spinner spnType;
    private EditText etDate, etDescribe, etAmount;
    private Button btnOk,btnCancel, btnDelete;
    private SingleItem item;
    DBHandler dbHandler;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String types[] = {"Breakfast", "Lunch", "Dinner", "Groceries", "Motorcycle", "Out Going", "Supper", "Other"};
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
        btnDelete = (Button) findViewById(R.id.btnNewDelete);
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
        ArrayAdapter adapter = new ArrayAdapter(Main2Activity.this, R.layout.support_simple_spinner_dropdown_item, types);
        spnType.setAdapter(adapter);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.amount = Integer.parseInt(etAmount.getText().toString());
                item.setDate(etDate.getText().toString());
                item.describe = etDescribe.getText().toString();
                item.type = spnType.getSelectedItem().toString();
                dbHandler.updateOrInsertDetail(item.toHashMap());
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Main2Activity.this);
                alert.setTitle("Are you sure to delete this record?")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHandler.deleteDetail(item.toHashMap());
                                finish();
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create().show();
            }
        });

        item = (SingleItem) getIntent().getSerializableExtra("item");
        if (item != null){
            etDate.setText(item.year + "-" + item.month +"-" + item.day);
            etAmount.setText(item.amount + "");
            etDescribe.setText(item.describe);
            for (int i = 0; i<types.length; i ++){
                if (types[i].equals(item.type)) {
                    spnType.setSelection(i);
                    break;
                }
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.33f);
            btnOk.setLayoutParams(lp);
            btnCancel.setLayoutParams(lp);
            btnDelete.setVisibility(View.VISIBLE);
        }else {
            item = new SingleItem();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
