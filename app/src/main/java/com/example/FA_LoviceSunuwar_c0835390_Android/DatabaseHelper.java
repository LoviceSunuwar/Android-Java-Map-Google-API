package com.example.FA_LoviceSunuwar_c0835390_Android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    //using Constant for column names

    private static final String DATABASE_NAME = "FavouritePlace";

    private static final int DATABASE_VERSION = 1;
    //   private static final String TABLE_NAME = "employees";
    private static final String TABLE_NAME = "favPlaces";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LATITUDE = "lat";
    private static final String COLUMN_LONGITUDE = "lng";
    private static final String COLUMN_ISVISITED = "isVisited";
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER NOT NULL CONSTRAINT employee_pk PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " varchar(200) NOT NULL, " +
                COLUMN_ADDRESS + " varchar(200), " +
                COLUMN_DATE + " varchar(200) NOT NULL, " +
                COLUMN_LATITUDE + " double NOT NULL, " +
                COLUMN_LONGITUDE + " double NOT NULL," +
                COLUMN_ISVISITED + " INTEGER NOT NULL); ";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(sql);
        onCreate(db);
    }

    boolean addFavPlace(String name,String date,String address, double lat, double lng) {

        //inorder to insert ,we need writable database;
        //this method returns a sqlite instance;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //contai value object
        ContentValues cv = new ContentValues();
        //this first argument of the put method is the columnn name and second value

        cv.put(COLUMN_NAME,name);
        cv.put(COLUMN_DATE,date);
        cv.put(COLUMN_ADDRESS,address);
        cv.put(COLUMN_LATITUDE,lng);
        cv.put(COLUMN_LONGITUDE,lat);
        cv.put(COLUMN_ISVISITED,0);
        //insert returns vallue of rownumber and -1 is not sucessfull ;

        return  sqLiteDatabase.insert(TABLE_NAME,null,cv)!= 1;

    }

    Cursor getAllPlace(){
        SQLiteDatabase sqLiteDatabase =getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME,null);

    }
    boolean updatePlace(int id,String name,double lat,String address,double lng,int isvisited){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();
        //this first argument of the put method is the columnn name and second value

        cv.put(COLUMN_NAME,name);
        cv.put(COLUMN_ADDRESS,address);
        cv.put(COLUMN_LATITUDE,lat);
        cv.put(COLUMN_LONGITUDE,lng);
        cv.put(COLUMN_ISVISITED,isvisited);

        //returns the affected num of rows;
        return  sqLiteDatabase.update(TABLE_NAME,cv,COLUMN_ID+" = ? ",new String[]{String.valueOf(id)}) >0 ;
    }

    boolean deletePlace(int id){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return  sqLiteDatabase.delete(TABLE_NAME,COLUMN_ID+" = ? ",new String[]{String.valueOf(id)}) >0;

    }
    boolean updateVisit(int id,int isvisited){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();
        //this first argument of the put method is the columnn name and second value

        cv.put(COLUMN_ISVISITED,isvisited);

        //returns the affected num of rows;
        return  sqLiteDatabase.update(TABLE_NAME,cv,COLUMN_ID+" = ? ",new String[]{String.valueOf(id)}) >0 ;
    }



    void delALL(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "DELETE FROM " +TABLE_NAME + ";";
        sqLiteDatabase.execSQL(sql);

    }

}

