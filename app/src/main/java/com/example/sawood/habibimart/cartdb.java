package com.example.sawood.habibimart;

public class cartdb {

    public static final String TABLE_NAME = "cart";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "Desc";
    public static final String COLUMN_PRICE = "Price";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_UPRICE = "uprice";
    public static final String COLOUMN_PRODID = "prodid";



    public static final String COLUMN_QTY = "qty";




    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TITLE + " TEXT UNIQUE, "
                    + COLUMN_DESC + " TEXT, "
                    + COLUMN_PRICE + " TEXT, "
                    + COLUMN_IMAGE + " TEXT, "
                    + COLUMN_UPRICE + " INTEGER, "
                    + COLUMN_QTY + " INTEGER, "
                    + COLOUMN_PRODID + " TEXT UNIQUE "
                    + ")";


    private int id;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public cartdb(){

    }

    public cartdb(int id, String title, String desc, String price, String image, int uprice, String prodid, int qty) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.image = image;
        this.uprice = uprice;
        this.prodid = prodid;
        this.qty = qty;
    }

    public void setPrice(String price) {

        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String desc;
    private String price;
    private String image;
    private int uprice;
    private String prodid;

    public int getUprice() {
        return uprice;
    }

    public void setUprice(int uprice) {
        this.uprice = uprice;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getProdid() {
        return prodid;
    }

    public void setProdid(String prodid) {
        this.prodid = prodid;
    }

    private int qty;



}
