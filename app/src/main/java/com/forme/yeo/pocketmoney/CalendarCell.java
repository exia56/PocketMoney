package com.forme.yeo.pocketmoney;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Yeo on 1/2/2017.
 */

public class CalendarCell extends LinearLayout {

    Context mContext;
    TextView tvDate, tvMoney;
    int year, month, day, money;

    public CalendarCell(Context context,int y, int m, int d, int $m) {
        super(context);
        mContext = context;
        year = y;
        month = m;
        day = d;
        money = $m;
        initialView();
    }

    private void initialView(){
        View view = View.inflate(mContext, R.layout.calendar_item, this);
        tvDate = (TextView)findViewById(R.id.ciDate);
        tvDate.setText(day + "");
        tvMoney = (TextView)findViewById(R.id.ciMoney);
        tvMoney.setText( money==0 ? "" : money + "");
    }

    public int getDay(){ return day; }
    public int getMonth(){ return month; }
    public int getYear(){ return year; }

    public TextView getTVDate(){
        return tvDate;
    }




}
