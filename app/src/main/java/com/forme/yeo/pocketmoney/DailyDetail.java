package com.forme.yeo.pocketmoney;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class DailyDetail extends AppCompatActivity {

    ListView lvDetail;

    List<HashMap<String, Object>> detailList;
    DBHandler dbHandler;
    DetailListAdapter detailListAdapter;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHandler = new DBHandler(this);

        year = getIntent().getIntExtra("year", -1);
        month = getIntent().getIntExtra("month", -1);
        day = getIntent().getIntExtra("day", -1);
        if (year == -1 || month == -1 || day == -1){
            AlertDialog.Builder alert = new AlertDialog.Builder(DailyDetail.this);
            alert.setTitle("Date information is incorrect")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create().show();
        }
        lvDetail = (ListView) findViewById(R.id.lvDetail);

        lvDetail.setOnItemClickListener(Button_Click);

        if (getActionBar() != null ){
            getActionBar().setTitle(year + "-" + month + "-" + day);
        }else if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(year + "-" + month + "-" + day);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        detailList = dbHandler.getDayDetail(year, month, day);
        detailListAdapter = new DetailListAdapter(this, detailList);
        lvDetail.setAdapter(detailListAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private AdapterView.OnItemClickListener Button_Click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Intent intent = new Intent(DailyDetail.this, Main2Activity.class);
            SingleItem item = new SingleItem(detailList.get(position));
            intent.putExtra("item", item);
            startActivity(intent);
        }
    };
}
