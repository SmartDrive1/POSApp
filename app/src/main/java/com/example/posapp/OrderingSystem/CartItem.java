package com.example.posapp.OrderingSystem;

public class CartItem {
    private String currentID;
    private String prodName;
    private String quantity;
    private String category;
    private String price;
    private String user;

    // Existing constructor
    public CartItem(String currentID, String prodName, int quantity, String category, double price, String user) {
        this.currentID = currentID;
        this.prodName = prodName;
        this.quantity = String.valueOf(quantity);
        this.category = category;
        this.price = String.valueOf(price);
        this.user = user;
    }

    // New constructor that accepts String values
    public CartItem(String currentID, String prodName, String quantity, String category, String price, String user) {
        this.currentID = currentID;
        this.prodName = prodName;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.user = user;

    }

    public String getId() {
        return currentID;
    }

    public String getProdName() {
        return prodName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public String getUser(){
        return user;
    }

    public String setUser(){
        return user;
    }
}
