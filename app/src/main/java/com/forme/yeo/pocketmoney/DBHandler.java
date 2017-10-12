package com.forme.yeo.pocketmoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.annotation.MainThread;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
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

    Context mContext;
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
        mContext = context;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//        importDB();
        db.execSQL("CREATE TABLE "+ TABLE_DETAIL+ "("+ DETAIL_ID + " TEXT PRIMARY KEY, " +
                DETAIL_YEAR+" INTEGER, "+ DETAIL_MONTH + " INTEGER, "+ DETAIL_DAY+ " INTEGER, "+
                DETAIL_TYPE+" TEXT, "+ DETAIL_DESCRIBE+" TEXT, "+DETAIL_AMOUNT+" INTEGER "+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_DETAIL);
        onCreate(db);
    }

    public boolean updateOrInsertDetail(HashMap<String, Object> singleItem){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db_read = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean idExisted = true;
        String temp_id = String.valueOf(singleItem.get(DETAIL_ID));
        String sql = "SELECT * " +
                " FROM " + TABLE_DETAIL +
                " WHERE " + DETAIL_ID + " = ? ";
        if (!db_read.rawQuery(sql, new String[]{temp_id}).moveToFirst()){
            idExisted = false;
        }
        contentValues.put(DETAIL_ID, temp_id);
        contentValues.put(DETAIL_YEAR, (int)(singleItem.get(DETAIL_YEAR)));
        contentValues.put(DETAIL_MONTH, (int)singleItem.get(DETAIL_MONTH));
        contentValues.put(DETAIL_DAY, (int)singleItem.get(DETAIL_DAY));
        contentValues.put(DETAIL_TYPE, String.valueOf(singleItem.get(DETAIL_TYPE)));
        contentValues.put(DETAIL_DESCRIBE, String.valueOf(singleItem.get(DETAIL_DESCRIBE)));
        contentValues.put(DETAIL_AMOUNT, (int)singleItem.get(DETAIL_AMOUNT));
        if (idExisted)
            db.update(TABLE_DETAIL, contentValues, DETAIL_ID + "= ?", new String[]{temp_id});
        else
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

    public List<HashMap<String, Object>> queryDetial (String str, String type){
        List<HashMap<String, Object>> singleItemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * " +
                " FROM " + TABLE_DETAIL;
        String describeSQL = "",typeSQL = "";
        String[] parameter = new String[0];
        if ( !str.equals("") ){
            describeSQL = DETAIL_DESCRIBE + " LIKE ? " ;
        }
        if ( !type.equals("ALL") ){
            typeSQL = DETAIL_TYPE + " LIKE ? " ;
        }
        if ( !str.equals("") && !type.equals("ALL") ){
            parameter = new String[]{ " N'%" + str + "%' " , " '%" + type + "%' "};
            Log.e(TABLE_DETAIL, parameter[0] + "\n" + parameter[1]);
            sql += " WHERE " + describeSQL + " AND " + typeSQL;
        }else if ( !str.equals("") ){
            parameter = new String[]{ " N'%" + str + "%' "};
            Log.e(TABLE_DETAIL, parameter[0]);
            sql += " WHERE " + describeSQL;
        }else if ( !type.equals("ALL") ){
            parameter = new String[]{" N'%" + type + "%' "};
            Log.e(TABLE_DETAIL, parameter[0]);
            sql += " WHERE " + typeSQL;
        }
        sql += " ORDER BY " + DETAIL_YEAR + ", " + DETAIL_MONTH + ", " + DETAIL_DAY + " DESC ";
        sql += " LIMIT 100 ";
        Log.e(TABLE_DETAIL, type);
        Log.e(TABLE_DETAIL, str);
        Log.e(TABLE_DETAIL, sql);
        Cursor cursor = db.rawQuery(sql, parameter);
        singleItemList.addAll(convertToList(cursor));
        db.close();
        return singleItemList;
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

    public void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        Log.d("DBDBDBDB SDSD", sd.getAbsolutePath());
        Log.d("DBDBDBDB DADA", data.getAbsolutePath());
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/data/"+ mContext.getPackageName() +"/databases/"+DATABASE_NAME;
        String backupDBPath = DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public boolean importDB(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = "/data/data/"+ mContext.getPackageName() +"/databases/"+DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){
            e.printStackTrace();
            //database does't exist yet.
        }
        if (checkDB == null){
            SQLiteDatabase s =  getReadableDatabase();
            s.close();
            try {
                //Open your local db as the input stream
                InputStream myInput = mContext.getAssets().open(DATABASE_NAME);

                // Path to the just created empty db
                String outFileName = "/data/data/"+ mContext.getPackageName() +"/databases/"+DATABASE_NAME;

                //Open the empty db as the output stream
                OutputStream myOutput = new FileOutputStream(outFileName);

                //transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer))>0){
                    myOutput.write(buffer, 0, length);
                }

                //Close the streams
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
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
