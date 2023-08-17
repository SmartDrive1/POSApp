package com.example.posapp;

public class transacProducts {
    private String productName;
    private double price;
    private int quantity;

    public transacProducts(String productName, Double price, int quantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters for attributes
    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String toString() {
        return "Product: " + productName + "\nPrice: Php " + price + "\nQuantity: " + quantity;
    }
}
