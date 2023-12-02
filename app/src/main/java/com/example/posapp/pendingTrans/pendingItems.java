package com.example.posapp.pendingTrans;

public class pendingItems {
    String transID;
    String status;
    String orderTime;

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public pendingItems(String transID, String status, String orderTime) {
        this.transID = transID;
        this.status = status;
        this.orderTime = orderTime;
    }

}
