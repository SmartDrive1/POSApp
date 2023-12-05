package com.example.posapp.OrderingSystem;

import com.google.firebase.firestore.PropertyName;

public class ProductData {
    private String prodName;
    private int quantity;
    private String category;
    private double price;

    // Default constructor required for Firestore
    public ProductData() {
    }

    public ProductData(String prodName, int quantity, String category, double price) {
        this.prodName = prodName;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
    }

    @PropertyName("prodName")
    public String getProdName() {
        return prodName;
    }

    @PropertyName("quantity")
    public int getQuantity() {
        return quantity;
    }

    @PropertyName("category")
    public String getCategory() {
        return category;
    }

    @PropertyName("price")
    public double getPrice() {
        return price;
    }
}