package com.example.posapp.OrderingSystem;

import com.google.firebase.firestore.PropertyName;

public class CartItem {
    @PropertyName("id")
    private String id;

    @PropertyName("prodName")
    private String prodName;

    @PropertyName("quantity")
    private int quantity;

    @PropertyName("category")
    private String category;

    @PropertyName("price")
    private double price;

    public CartItem() {
        // Default constructor required for Firestore
    }

    public CartItem(String id, String prodName, int quantity, String category, double price) {
        this.id = id;
        this.prodName = prodName;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getProdName() {
        return prodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }
}
