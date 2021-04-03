package com.example.canteenautomationsystem.Model;

public class Users {

    private String name;
    private String phone;
    private String uid;
    private String email;
    private String due;
    private String wallet;
    private String image;


    public Users(String name, String phone, String uid, String email, String due, String wallet, String image) {
        this.name = name;
        this.phone = phone;
        this.uid = uid;
        this.email = email;
        this.due = due;
        this.wallet = wallet;
        this.image = image;
    }

    public Users(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
