package com.forme.yeo.pocketmoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.MainThread;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Yeo on 10/2/2016.
 */
public class DBHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PocketMoney";
    public static final String TABLE_DETAIL = "detail";

    public static final String DETAIL_ID = "_id";
    public static final String DETAIL_YEAR = "year";
    public static final String DETAIL_MONTH = "month";
    public static final String DETAIL_DAY = "day";
    public static final String DETAIL_TYPE = "type";
    public static final String DETAIL_DESCRIBE = "describe";
    public static final String DETAIL_AMOUNT = "amount";


    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_DETAIL+ "("+ DETAIL_ID + " TEXT PRIMARY KEY, " +
                DETAIL_YEAR+" INTEGER, "+ DETAIL_MONTH + " INTEGER, "+ DETAIL_DAY+ " INTEGER, "+
                DETAIL_TYPE+" TEXT, "+ DETAIL_DESCRIBE+" TEXT, "+DETAIL_AMOUNT+" INTEGER "+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_DETAIL);
        onCreate(db);
    }

    public boolean insertDetail (HashMap<String, Object> singleItem){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db_read = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        String temp_id = String.valueOf(singleItem.get(DETAIL_ID));
        String sql = "SELECT * " +
                " FROM " + TABLE_DETAIL +
                " WHERE " + DETAIL_ID + " = ? ";
        while (true){
            if (!db_read.rawQuery(sql, new String[]{temp_id}).moveToFirst()){
                break;
            }
            temp_id = genID();
            Log.e("!!!!!!!!!!!!!!", temp_id);
        }
        contentValues.put(DETAIL_ID, temp_id);
        contentValues.put(DETAIL_YEAR, (int)(singleItem.get(DETAIL_YEAR)));
        contentValues.put(DETAIL_MONTH, (int)singleItem.get(DETAIL_MONTH));
        contentValues.put(DETAIL_DAY, (int)singleItem.get(DETAIL_DAY));
        contentValues.put(DETAIL_TYPE, String.valueOf(singleItem.get(DETAIL_TYPE)));
        contentValues.put(DETAIL_DESCRIBE, String.valueOf(singleItem.get(DETAIL_DESCRIBE)));
        contentValues.put(DETAIL_AMOUNT, (int)singleItem.get(DETAIL_AMOUNT));
        db.insert(TABLE_DETAIL, null, contentValues);
        db.close();
        return true;
    }

    public boolean updateDetail (HashMap<String, Object> singleItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DETAIL_YEAR, (int)(singleItem.get(DETAIL_YEAR)));
        contentValues.put(DETAIL_MONTH, (int)singleItem.get(DETAIL_MONTH));
        contentValues.put(DETAIL_DAY, (int)singleItem.get(DETAIL_DAY));
        contentValues.put(DETAIL_TYPE, String.valueOf(singleItem.get(DETAIL_TYPE)));
        contentValues.put(DETAIL_DESCRIBE, String.valueOf(singleItem.get(DETAIL_DESCRIBE)));
        contentValues.put(DETAIL_AMOUNT, (int)singleItem.get(DETAIL_AMOUNT));
        db.update(TABLE_DETAIL, contentValues, DETAIL_ID+" = ?", new String[]{String.valueOf(singleItem.get(DETAIL_ID))});
        db.close();
        return true;
    }

    public boolean deleteDetail (HashMap<String, Object> singleItem){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETAIL, DETAIL_ID+" = ?", new String[]{String.valueOf(singleItem.get(DETAIL_ID))});
        db.close();
        return true;
    }

    public boolean cleanDetail (){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 1, 1);
        db.close();
        return true;
    }

    public List<HashMap<String, Object>> getAmountForCV (int year, int m1, int d1, int m2, int d2){
        List<HashMap<String, Object>> singleItemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + DETAIL_YEAR + ", " + DETAIL_MONTH + ", " + DETAIL_DAY + ", SUM(" + DETAIL_AMOUNT + ") as Total" +
                " FROM " + TABLE_DETAIL +
                " WHERE " + DETAIL_YEAR + " = ? AND " + DETAIL_MONTH + " = ? AND " + DETAIL_DAY + " >= ? " +
                " GROUP BY " + DETAIL_YEAR + ", " + DETAIL_MONTH + ", " + DETAIL_DAY;
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(m1==12?year-1:year), String.valueOf(m1), String.valueOf(d1)});
        singleItemList.addAll(convertToList(cursor));
        sql = "SELECT " + DETAIL_YEAR + ", " + DETAIL_MONTH + ", " + DETAIL_DAY + ", SUM(" + DETAIL_AMOUNT + ") as Total" +
                " FROM " + TABLE_DETAIL +
                " WHERE " + DETAIL_YEAR + " = ? AND " + DETAIL_MONTH + " = ? " +
                " GROUP BY " + DETAIL_YEAR + ", " + DETAIL_MONTH + ", " + DETAIL_DAY;
        cursor = db.rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(m1==12?1:m1+1)});
        singleItemList.addAll(convertToList(cursor));
        sql = "SELECT " + DETAIL_YEAR + ", " + DETAIL_MONTH + ", " + DETAIL_DAY + ", SUM(" + DETAIL_AMOUNT + ") as Total" +
                " FROM " + TABLE_DETAIL +
                " WHERE " + DETAIL_YEAR + " = ? AND " + DETAIL_MONTH + " = ? AND " + DETAIL_DAY + " <= ? "+
                " GROUP BY " + DETAIL_YEAR + ", " + DETAIL_MONTH + ", " + DETAIL_DAY;
        cursor = db.rawQuery(sql, new String[]{String.valueOf(m2==1?year+1:year), String.valueOf(m2), String.valueOf(d2)});
        singleItemList.addAll(convertToList(cursor));
        db.close();
        return singleItemList;
    }

    public List<HashMap<String, Object>> getMonthDetail (int y, int m){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * " +
                " FROM " + TABLE_DETAIL +
                " WHERE " + DETAIL_YEAR + " = ? AND " + DETAIL_MONTH + " = ?" ;
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(y), String.valueOf(m)});
        List<HashMap<String, Object>> getList = convertToList(cursor);
        db.close();
        return getList;
    }

    public List<HashMap<String, Object>> getDayDetail (int y, int m, int d){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * " +
                " FROM " + TABLE_DETAIL +
                " WHERE " + DETAIL_YEAR + " = ? AND " + DETAIL_MONTH + " = ? AND " + DETAIL_DAY + " = ?"  ;
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(y), String.valueOf(m), String.valueOf(d)});
        List<HashMap<String, Object>> getList = convertToList(cursor);
        db.close();
        return getList;
    }

    public int getMonthAmount (int y, int m){
        SQLiteDatabase db = this.getReadableDatabase();
        int returnAmount = -1;
        String sql = "SELECT " + " SUM(" + DETAIL_AMOUNT + ") as Total" +
                " FROM " + TABLE_DETAIL +
                " WHERE " + DETAIL_YEAR + " = ? AND " + DETAIL_MONTH + " = ? " +
                " GROUP BY " + DETAIL_YEAR + ", " + DETAIL_MONTH;
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(y), String.valueOf(m)});
        if (cursor.moveToFirst()){
            returnAmount = cursor.getInt(0);
        }
        db.close();
        return returnAmount;
    }

    private HashMap<String, Object> genItem(Cursor cursor){
        HashMap<String, Object> item = new HashMap<>();
        int len = cursor.getColumnNames().length;
        while (--len >= 0){
            item.put(cursor.getColumnName(len), getObject(cursor, len));
        }
        return item;
    }

    private Object getObject(Cursor cursor, int position){
        switch (cursor.getType(position)){
            case Cursor.FIELD_TYPE_INTEGER:
                return cursor.getInt(position);
            case Cursor.FIELD_TYPE_STRING:
                return cursor.getString(position);
            case Cursor.FIELD_TYPE_FLOAT:
                return cursor.getFloat(position);
            case Cursor.FIELD_TYPE_BLOB:
                return cursor.getBlob(position);
            case Cursor.FIELD_TYPE_NULL:
            default:
                return null;
        }
    }

    private List<HashMap<String, Object>> convertToList(Cursor cursor){
        List<HashMap<String, Object>> getList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                getList.add(genItem(cursor));
            }while (cursor.moveToNext());
        }
        return getList;
    }

    private String genID(){
        long s = Calendar.getInstance().getTime().getTime() + (long)(Math.random() * 10000);
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

}
