package com.example.posapp.OrderingSystem;

public class OSItems {
    String id;
    String pName;
    String pQuantity;
    String pPrice;
    String category;

    public OSItems(String id, String pName, String pQuantity, String pPrice, String category) {
        this.id = id;
        this.pName = pName;
        this.pQuantity = pQuantity;
        this.pPrice = pPrice;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
