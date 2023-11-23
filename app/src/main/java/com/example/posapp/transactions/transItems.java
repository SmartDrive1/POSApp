package com.example.posapp.transactions;

public class transItems {
    String transID;
    String pQuantity;
    String pPrice;
    String tDate;
    String pCategory;


    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
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

    public String gettDate() {
        return tDate;
    }

    public void settDate(String tDate) {
        this.tDate = tDate;
    }

    public String getpCategory() {
        return pCategory;
    }

    public void setpCategory(String pCategory) {
        this.pCategory = pCategory;
    }

    public transItems(String transID, String pName, String pQuantity, String pPrice, String pCategory, String tDate) {
        this.transID = transID;
        this.pQuantity = String.valueOf(pQuantity);
        this.pPrice = String.valueOf(pPrice);
        this.pCategory = String.valueOf(pCategory);
        this.tDate = tDate;
    }
}
