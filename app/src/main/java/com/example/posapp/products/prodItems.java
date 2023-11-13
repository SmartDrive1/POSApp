package com.example.posapp.products;

public class prodItems {
    String id;
    String product;
    String category;
    String prodPrice;
    String quantity;

    public prodItems(String id, String product, String category, String prodPrice, String quantity) {
        this.id = id;
        this.product = product;
        this.category = category;
        this.prodPrice = prodPrice;
        this.quantity = quantity;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
