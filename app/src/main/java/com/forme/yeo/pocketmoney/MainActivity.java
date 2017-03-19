package com.forme.yeo.pocketmoney;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int cur_year = 0;
    private int cur_month = 0;
    private int cur_day = 0;
    private int jumpMonth = 0, jumpYear = 0;
    private String strCurrentDate = "";

    private GridView gridView;
    private TextView btnNowMonth;
    private Button btnLastMonth, btnNextMonth;
    private TextView tvMonthAmount;

    private DBHandler dbHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnLastMonth = (Button) findViewById(R.id.lastMonth);
        btnNextMonth = (Button) findViewById(R.id.nextMonth);
        btnNowMonth = (TextView) findViewById(R.id.nowMonth);
        tvMonthAmount = (TextView) findViewById(R.id.monthAmount);
        gridView = (GridView)findViewById(R.id.viewForCalendar);

        dbHandler = new DBHandler(MainActivity.this);
        initialView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                final EditText pwd = new EditText(MainActivity.this);
                alert.setView(pwd)
                        .setTitle("input password to delete all data")
                        .setMessage("Password:")
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strPWD = pwd.getText().toString();
                                if (strPWD.equals("5693")){
                                    dbHandler.cleanDetail();
                                }
                            }
                        })
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast t = new Toast(MainActivity.this);
                                t.setText("ok");
                                t.setDuration(Toast.LENGTH_SHORT);
                                t.show();
                            }
                        })
                        .create().show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private void initialView(){
        setListener();
        getCurrentDate();
        changeGridView();
    }

    public View.OnClickListener changePageEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewId = v.getId();
                switch (viewId){
                    case R.id.lastMonth:
                        jumpMonth--;
                        if (jumpMonth <= 0){
                            jumpMonth = 12;
                            jumpYear--;
                        }
                        changeGridView();
                        break;
                    case R.id.nextMonth:
                        jumpMonth++;
                        if (jumpMonth > 12) {
                            jumpMonth = 1;
                            jumpYear++;
                        }
                        changeGridView();
                        break;
                    default:

                }
            }
        };
    }

    public AdapterView.OnItemClickListener itemClickEvent(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DailyDetail.class);
                CalendarCell calendarCell = (CalendarCell) view;
                intent.putExtra("year", calendarCell.getYear());
                intent.putExtra("month", calendarCell.getMonth());
                intent.putExtra("day", calendarCell.getDay());
                startActivity(intent);
            }
        };
    }

    private void setListener(){
        btnLastMonth.setOnClickListener(changePageEvent());
        btnNextMonth.setOnClickListener(changePageEvent());
    }

    private void getCurrentDate(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        strCurrentDate = sdf.format(date);
        jumpYear = cur_year = Integer.parseInt(strCurrentDate.split("-")[0]);
        jumpMonth = cur_month = Integer.parseInt(strCurrentDate.split("-")[1]);
        cur_day = Integer.parseInt(strCurrentDate.split("-")[2]);
    }

    private void changeGridView(){
        CalendarAdapter ca = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear);
        tvMonthAmount.setText(dbHandler.getMonthAmount(jumpYear, jumpMonth) + "");
        btnNowMonth.setText(jumpYear + "-" + jumpMonth);
        gridView.setAdapter(ca);
        gridView.setOnItemClickListener(itemClickEvent());
    }

}
