package com.example.canteenautomationsystem.Model;

public class Meals {

    private String Name;
    private String Image; private String Price;
    private String Discount;

    public Meals(String name, String image, String price, String discount) {
        Name = name;
        Image = image;
        Price = price;
        Discount = discount;
    }

    public Meals(){
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
