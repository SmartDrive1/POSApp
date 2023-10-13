package com.example.posapp;

public class UITestItems {
    String id;
    String product;
    String category;
    String prodPrice;

    public UITestItems(String id, String product, String category, String prodPrice) {
        this.id = id;
        this.product = product;
        this.category = category;
        this.prodPrice = prodPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }
}
