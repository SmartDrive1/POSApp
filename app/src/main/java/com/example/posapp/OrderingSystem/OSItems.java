package com.example.posapp.OrderingSystem;

public class OSItems {
    String id;
    String pName;
    String pQuantity;
    String pPrice;

    public OSItems(String id, String pName, String pQuantity, String pPrice) {
        this.id = id;
        this.pName = pName;
        this.pQuantity = pQuantity;
        this.pPrice = pPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpQuantity() {
        return pQuantity;
    }

    public void setpQuantity(String pQuantity) {
        this.pQuantity = pQuantity;
    }

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }
}
