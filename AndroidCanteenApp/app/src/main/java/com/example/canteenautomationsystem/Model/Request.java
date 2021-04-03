package com.example.canteenautomationsystem.Model;

import java.util.List;

public class Request {

    private String oid;
    private String phone;
    private String name;
    private String email;
    private String uid;
    private String date;
    private String total;
    private String status;
    private List<Order> foods;

    public Request(){
    }

    public Request(String oid,String phone, String name, String email,String uid, String date, String total, List<Order> foods) {
        this.oid = oid;
        this.phone = phone;
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.date = date;
        this.total = total;
        this.status = "0";   // Default is 0 ( 0: Placed, 1: Shipping, 2: Shipped)
        this.foods = foods;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
