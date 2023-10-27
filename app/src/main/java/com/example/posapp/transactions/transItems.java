package com.example.posapp.transactions;

public class transItems {
    String transID;
    String pName;
    String pQuantity;
    String pPrice;
    String tDate;


    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
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

    public String gettDate() {
        return tDate;
    }

    public void settDate(String tDate) {
        this.tDate = tDate;
    }

    public transItems(String transID, String pName, String pQuantity, String pPrice, String tDate) {
        this.transID = transID;
        this.pName = pName;
        this.pQuantity = String.valueOf(pQuantity);
        this.pPrice = String.valueOf(pPrice);
        this.tDate = tDate;
    }
}
