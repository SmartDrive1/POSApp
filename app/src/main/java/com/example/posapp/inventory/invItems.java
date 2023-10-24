package com.example.posapp.inventory;

public class invItems {
    String itemID;
    String itemName;
    String itemStock;

    public invItems(String itemID, String itemName, String itemStock) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemStock = itemStock;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemStock() {
        return itemStock;
    }

    public void setItemStock(String itemStock) {
        this.itemStock = itemStock;
    }
}
