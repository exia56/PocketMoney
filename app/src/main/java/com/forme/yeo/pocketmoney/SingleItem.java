package com.forme.yeo.pocketmoney;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Yeo on 10/9/2016.
 */
public class SingleItem implements Serializable {
    public int amount;
    public int year;
    public int month;
    public int day;
    public String type;
    public String describe;
    public String _id;

    public SingleItem (){
        _id = genID();
    }
    public SingleItem (String id, int y, int m, int d, String de, String t, int a){
        _id = id;
        year = y;
        month = m;
        day = d;
        describe = de;
        type = setType(t);
        amount = a;
    }

    public SingleItem (int y, int m, int d, String de, String t, int a){
        _id = genID();
        year = y;
        month = m;
        day = d;
        describe = de;
        type = setType(t);
        amount = a;
    }

    public SingleItem (String date, String de, String t, int a){
        _id = genID();
        setDate(date);
        describe = de;
        type = setType(t);
        amount = a;
    }

    public SingleItem(HashMap<String, Object> item){
        _id = String.valueOf(item.get(DBHandler.DETAIL_ID));
        year = (int)item.get(DBHandler.DETAIL_YEAR);
        month = (int)item.get(DBHandler.DETAIL_MONTH);
        day = (int)item.get(DBHandler.DETAIL_DAY);
        describe = String.valueOf(item.get(DBHandler.DETAIL_DESCRIBE));
        type = setType(String.valueOf(item.get(DBHandler.DETAIL_TYPE)));
        amount = (int)item.get(DBHandler.DETAIL_AMOUNT);
    }

    public void setDate(String date){
        String[] s = date.split("-");
        year = Integer.parseInt(s[0]);
        month = Integer.parseInt(s[1]);
        day = Integer.parseInt(s[2]);
    }

    private String setType(String original){
        switch (original){
            case "Breakfast":
                break;
            case "Lunch":
                break;
            case "Dinner":
                break;
            case "Groceries":
                break;
            case "Motorcycle":
                break;
            case "Out Going":
                break;
            case "Supper":
                break;
            default:
                original = "Other";
        }
        return original;
    }

    private String genID(){
        long s = Calendar.getInstance().getTime().getTime()+ (long)(Math.random()*10000);
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(String.valueOf(s).getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest){
                String h = Integer.toHexString((0xFF & b) | 0x100).substring(1, 3);
                hexString.append(h);

            }
            return  hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, Object> toHashMap(){
        HashMap<String, Object> item = new HashMap<>();
        item.put(DBHandler.DETAIL_ID, _id);
        item.put(DBHandler.DETAIL_YEAR, year);
        item.put(DBHandler.DETAIL_MONTH, month);
        item.put(DBHandler.DETAIL_DAY, day);
        item.put(DBHandler.DETAIL_DESCRIBE, describe);
        item.put(DBHandler.DETAIL_TYPE, type);
        item.put(DBHandler.DETAIL_AMOUNT, amount);
        return item;
    }

    public String toString(){
        return "{ _id: " + _id + "\n" +
                "year: " + year + "\n" +
                "month: " + month + "\n" +
                "day: " + day + "\n" +
                "type: " + type + "\n" +
                "describe: " + describe + "\n" +
                "amount: " + amount + " }\n" ;
    }
}
