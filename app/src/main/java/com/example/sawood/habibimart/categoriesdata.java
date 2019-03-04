package com.example.sawood.habibimart;

public class categoriesdata {

    private String catname;
    private String catdesc;

    public categoriesdata() {
    }

    public categoriesdata(String catname, String catdesc, String catimage, String offer) {
        this.catname = catname;
        this.catdesc = catdesc;
        this.catimage = catimage;
        this.offer = offer;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getCatdesc() {
        return catdesc;
    }

    public void setCatdesc(String catdesc) {
        this.catdesc = catdesc;
    }

    public String getCatimage() {
        return catimage;
    }

    public void setCatimage(String catimage) {
        this.catimage = catimage;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    private String catimage;
    private String offer;
}
