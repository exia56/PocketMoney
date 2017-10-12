package com.forme.yeo.pocketmoney;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ImageButton btnSearch;
    EditText etSearch;
    Spinner spnType;
    ListView lvDetail;

    DBHandler dbHandler;
    List<HashMap<String, Object>> detailList;
    DetailListAdapter detailListAdapter;

    private String types[] = { "ALL", "Breakfast", "Lunch", "Dinner", "Groceries", "Motorcycle", "Out Going", "Supper", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHandler = new DBHandler(this);
        etSearch = (EditText) findViewById(R.id.etSearch);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        spnType = (Spinner) findViewById(R.id.spnType);
        lvDetail = (ListView) findViewById(R.id.lvDetail);
        detailList = new ArrayList<>();
        detailListAdapter = new DetailListAdapter(this, detailList);
        lvDetail.setAdapter(detailListAdapter);

        ArrayAdapter adapter = new ArrayAdapter(SearchActivity.this, R.layout.support_simple_spinner_dropdown_item, types);
        spnType.setAdapter(adapter);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailList = dbHandler.queryDetial(etSearch.getText().toString(), spnType.getSelectedItem().toString());
                detailListAdapter.update(detailList);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        detailList = dbHandler.queryDetial(etSearch.getText().toString(), spnType.getSelectedItem().toString());
        detailListAdapter.update(detailList);
    }

}
