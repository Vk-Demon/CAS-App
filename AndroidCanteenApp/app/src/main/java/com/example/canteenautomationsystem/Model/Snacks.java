package com.example.canteenautomationsystem.Model;

public class Snacks {

    private String Name;
    private String Image;
    private String Price;
    private String Discount;

    public Snacks(String name, String image, String price, String discount) {
        Name = name;
        Image = image;
        Price = price;
        Discount = discount;
    }

    public Snacks(){
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
