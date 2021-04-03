package com.example.canteenautomationsystem.Model;

public class Favourites {

    private String email;
    private String emailcat;
    private String catid;
    private String catname;
    private String item;
    private String image;
    private String price;
    private String discount;

    public Favourites(){
    }

    public Favourites(String email, String emailcat, String catid, String catname, String item, String image, String price, String discount) {
        this.email = email;
        this.emailcat = emailcat;
        this.catid = catid;
        this.catname = catname;
        this.item = item;
        this.image = image;
        this.price = price;
        this.discount = discount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmailcat() {
        return emailcat;
    }

    public void setEmailcat(String emailcat) {
        this.emailcat = emailcat;
    }
}
