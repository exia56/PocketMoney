package com.forme.yeo.pocketmoney;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Yeo on 3/19/2017.
 */

public class DetailListAdapter extends BaseAdapter {
    Context mContext;
    List<HashMap<String, Object>> detailList;

    public DetailListAdapter(Context c, List<HashMap<String, Object>> l){
        mContext = c;
        detailList = l;
    }

    public void update (List<HashMap<String, Object>> l){
        detailList = l;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return detailList.size();
    }

    @Override
    public Object getItem(int position) {
        return detailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_detail_item, null);
        SingleItem singleItem = new SingleItem(detailList.get(position));
        TextView tvMoney = (TextView) convertView.findViewById(R.id.tvMoney);
        TextView tvDescribe = (TextView) convertView.findViewById(R.id.tvDescribe);
        TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
        tvMoney.setText(singleItem.amount + "");
        tvDescribe.setText(singleItem.describe);
        tvType.setText(singleItem.type);
        return convertView;
    }


}
