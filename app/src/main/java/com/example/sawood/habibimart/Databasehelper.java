package com.example.sawood.habibimart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Databasehelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "cart";
    public static final String COLUMN_TITLE = "title";

    // Database Name
    private static final String DATABASE_NAME = "cart_dab";
    public Databasehelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public Databasehelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
    public Databasehelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(cartdb.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + cartdb.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }



    public Long insertprod(String title, String desc, String price, String img, int uprice, int qty,String prodid) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(cartdb.COLUMN_TITLE, title);
        values.put(cartdb.COLUMN_DESC,desc);
        values.put(cartdb.COLUMN_PRICE,price);
        values.put(cartdb.COLUMN_IMAGE,img);
        values.put(cartdb.COLUMN_UPRICE,uprice);
        values.put(cartdb.COLUMN_QTY,qty);
        values.put(cartdb.COLOUMN_PRODID,prodid);


        // insert row
        Long id = db.insertWithOnConflict(cartdb.TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_IGNORE);

        // close db connection
        db.close();
        Log.e("data","inserted");

        // return newly inserted row id
        return id;
    }

    public List<cartpro> getallproducts(){
        List<cartpro> cartproducts = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + cartdb.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                cartpro ct = new cartpro();
               // ct.setId(cursor.getInt(cursor.getColumnIndex(cartdb.COLUMN_ID)));
                ct.setTitle(cursor.getString(cursor.getColumnIndex(cartdb.COLUMN_TITLE)));
                ct.setDescription(cursor.getString(cursor.getColumnIndex(cartdb.COLUMN_DESC)));
                ct.setPrice(cursor.getString(cursor.getColumnIndex(cartdb.COLUMN_PRICE)));
                ct.setImage(cursor.getString(cursor.getColumnIndex(cartdb.COLUMN_IMAGE)));
                ct.setUpdatedprice(cursor.getInt(cursor.getColumnIndex(cartdb.COLUMN_UPRICE)));
                ct.setQty(cursor.getInt(cursor.getColumnIndex(cartdb.COLUMN_QTY)));
                ct.setProdid(cursor.getString(cursor.getColumnIndex(cartdb.COLOUMN_PRODID)));
                cartproducts.add(ct);
                Log.e("query",  DatabaseUtils.dumpCursorToString(cursor));
            } while (cursor.moveToNext());
        }


        // close db connection
        db.close();

     return cartproducts;


    }

    public void delete(String s){

        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_NAME,COLUMN_TITLE + " = " + s,null);
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+COLUMN_TITLE+"='"+s+"'");
        Log.e("item:","deleted");
        db.close();
    }

    public void update(String title,String desc,String price,String img,int uprice,int qty){
        int s1 = 0,s2 = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String selecting = "SELECT * FROM " + cartdb.TABLE_NAME + " WHERE "+COLUMN_TITLE+"='"+title+"'";
        Cursor cursor = db.rawQuery(selecting, null);
        if (cursor.moveToFirst()) {
            do {
                s1 = cursor.getInt(cursor.getColumnIndex(cartdb.COLUMN_UPRICE));
                s2 = cursor.getInt(cursor.getColumnIndex(cartdb.COLUMN_QTY));
                Log.e("query",  DatabaseUtils.dumpCursorToString(cursor));
            } while (cursor.moveToNext());
        }
        s1 = s1+uprice;
        s2 = s2 +qty;
        String[] args = {title};
        ContentValues values = new ContentValues();
        values.put(cartdb.COLUMN_UPRICE,s1);
        values.put(cartdb.COLUMN_QTY,s2);
        db.update(cartdb.TABLE_NAME,values,"title=?",args);

        Log.e(String.valueOf(s1),String.valueOf(s2));
        db.close();




    }

}
