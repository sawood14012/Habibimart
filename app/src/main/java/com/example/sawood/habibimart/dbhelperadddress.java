package com.example.sawood.habibimart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class dbhelperadddress extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "address";
    public static final String COLUMN_ADDR = "address";

    // Database Name
    private static final String DATABASE_NAME = "address_db";
    public dbhelperadddress(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public dbhelperadddress(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public dbhelperadddress(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(addressdb.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + addressdb.TABLE_NAME);

        // Create tables again
        onCreate(db);

    }


    public long add_address(String address){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(addressdb.COLUMN_ADDR,address);
        Log.e("str",address);
        long id = db.insertWithOnConflict(addressdb.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        return id;
    }


    public List<addressstore> getalladdress()
    {
        List<addressstore> add = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + addressdb.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                addressstore ad = new addressstore();
                ad.setAddress(cursor.getString(cursor.getColumnIndex(addressdb.COLUMN_ADDR)));
                add.add(ad);
                Log.e("query",  DatabaseUtils.dumpCursorToString(cursor));

            } while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        return add;

    }


    public void delete(String s){

        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_NAME,COLUMN_TITLE + " = " + s,null);
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+COLUMN_ADDR+"='"+s+"'");
        Log.e("item:","deleted");
        db.close();
    }


}
