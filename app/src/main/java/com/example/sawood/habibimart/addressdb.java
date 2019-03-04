package com.example.sawood.habibimart;

public class addressdb {

    public static final String TABLE_NAME = "address";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ADDR = "address";


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ADDR + " TEXT UNIQUE NOT NULL"
                    + ")";


    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public addressdb(int id, String address) {
        this.id = id;
        this.address = address;
    }

    String address;
}
