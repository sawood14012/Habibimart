package com.example.sawood.habibimart;

public class cartpro {
    private String title;
    private String description;
    private String prodid;

    public int getUpdatedprice() {
        return updatedprice;
    }

    public void setUpdatedprice(int updatedprice) {
        this.updatedprice = updatedprice;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    private int updatedprice;
    private int qty;



    private String image;
    private String price;
    public cartpro() {
    }
    public cartpro(String title, String description, String prodid, String image, String price, int updatedprice, int qty) {
        this.title = title;
        this.description = description;
        this.prodid = prodid;
        this.image = image;
        this.price = price;
        this.updatedprice = updatedprice;
        this.qty = qty;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProdid() {
        return prodid;
    }

    public void setProdid(String prodid) {
        this.prodid = prodid;
    }
}


