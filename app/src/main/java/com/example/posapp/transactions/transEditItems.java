package com.example.posapp.transactions;

public class transEditItems {
    String pName;
    String pQuantity;
    String pPrice;
    String pCategory;

    public transEditItems(String pName, String pQuantity, String pCategory, String pPrice) {
        this.pName = pName;
        this.pQuantity = pQuantity;
        this.pPrice = pPrice;
        this.pCategory = pCategory;
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

    public String getpCategory() {
        return pCategory;
    }

    public void setpCategory(String pCategory) {
        this.pCategory = pCategory;
    }
}
