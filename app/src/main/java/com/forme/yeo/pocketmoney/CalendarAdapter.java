package com.forme.yeo.pocketmoney;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yeo on 9/18/2016.
 */
public class CalendarAdapter extends BaseAdapter{
    Context mContext;
    Resources res;
    int[] everyDay = new int[42];
    int[] everyDayTotal = new int[42];
    int idx_start = 0, idx_end = 0;
    DBHandler dbHandler = null;
    public CalendarAdapter(Context context, Resources rs, int showMonth, int showYear){
        mContext = context;
        dbHandler = new DBHandler(mContext);
        res = rs;
        /*
                Calendar形態適用
                日期的：年是直接輸入
                        月是0～11
                        日是1～31
         */
        SimpleDateFormat sdfForWeek = new SimpleDateFormat("EEE");
        SimpleDateFormat sdfForDay = new SimpleDateFormat("dd");
        Calendar calendar = Calendar.getInstance();
        int lastEndDay = 0, thisEndDay = 0;

        calendar.set(showYear, showMonth-1, 0);
        int startWeek = (getWeekday(sdfForWeek.format(calendar.getTime()))+1)%7;
        lastEndDay = (Integer.parseInt(sdfForDay.format(calendar.getTime())));

        calendar.set(showYear, showMonth+1-1, 0);
        thisEndDay = Integer.parseInt(sdfForDay.format(calendar.getTime()));
        int endWeek = getWeekday(sdfForWeek.format(calendar.getTime()));
        int i = 0, offset = 7;
        if (startWeek>=4) offset = 0;
        lastEndDay = lastEndDay-startWeek-offset;
        for (; i < startWeek+offset; i++ )
            everyDay[i] = lastEndDay+1+i;
        idx_start = i;
        for (; i < thisEndDay+startWeek+offset; i++ )
            everyDay[i] = i-startWeek-offset+1;
        idx_end = i;
        for (; i < everyDay.length; i++ )
            everyDay[i] = (i-startWeek-offset-thisEndDay)+1;

        List<HashMap<String, Object>> everydaylist =  dbHandler.getAmountForCV(showYear, showMonth==1?12:showMonth-1, everyDay[0], (showMonth==12)?1:showMonth+1, everyDay[41]);
        for (i = 0 ; i < everydaylist.size() ; i++){
            HashMap<String, Object> item = everydaylist.get(i);
            if ((int)item.get(DBHandler.DETAIL_MONTH) == showMonth) {
                everyDayTotal[startWeek + offset + ((int) item.get(DBHandler.DETAIL_DAY)) - 1] = (int) item.get("Total");
            }else if ((int)item.get(DBHandler.DETAIL_MONTH) == (showMonth+1) || ((int)item.get(DBHandler.DETAIL_MONTH)-showMonth)<-1 ) {
                everyDayTotal[startWeek + offset + thisEndDay + ((int) item.get(DBHandler.DETAIL_DAY)) - 1] = (int) item.get("Total");
            }else if ((int)item.get(DBHandler.DETAIL_MONTH) == (showMonth-1) || ((int)item.get(DBHandler.DETAIL_MONTH)-showMonth)>1) {
                everyDayTotal[((int) item.get(DBHandler.DETAIL_DAY)) - lastEndDay - 1] = (int) item.get("Total");
            }
        }
//        for (i=0; i <everyDayTotal.length; i++)
//            Log.e("day("+(i+1)+")", everyDayTotal[i]+"");
    }

    private int getWeekday(String str){
        int startWeek= -1;
        switch(str){
            case "Sun":
                startWeek = 0;
                break;
            case "Mon":
                startWeek = 1;
                break;
            case "Tue":
                startWeek = 2;
                break;
            case "Wed":
                startWeek = 3;
                break;
            case "Thu":
                startWeek = 4;
                break;
            case "Fri":
                startWeek = 5;
                break;
            case "Sat":
                startWeek = 6;
                break;
        }
        return startWeek;
    }


    @Override
    public int getCount() {
        return 42;
    }

    @Override
    public Object getItem(int position) {return null;

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.calendar_item, null);
        }
        TextView tv_money = (TextView) convertView.findViewById(R.id.money);
        TextView tv_date = (TextView) convertView.findViewById(R.id.date);
        tv_money.setText(everyDayTotal[position] + "");
        tv_date.setText(everyDay[position] + "");
        if (position % 7 == 0 || position % 7 == 6){
            tv_date.setTextColor(Color.rgb(224, 0, 0));
            tv_date.setTypeface(null, Typeface.BOLD);
        }

        if (position < idx_start || position >= idx_end){
            tv_date.setTextColor(Color.rgb(200, 200, 200));
        }

        return convertView;
    }
}
