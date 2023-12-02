package com.example.posapp.pendingTrans;

public class orderEditItems {
    String prodName;
    String pQuantity;

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getpQuantity() {
        return pQuantity;
    }

    public void setpQuantity(String pQuantity) {
        this.pQuantity = pQuantity;
    }

    public orderEditItems(String prodName, String pQuantity) {
        this.prodName = prodName;
        this.pQuantity = pQuantity;
    }
}
