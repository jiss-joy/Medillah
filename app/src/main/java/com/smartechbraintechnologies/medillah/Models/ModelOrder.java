package com.smartechbraintechnologies.medillah.Models;

public class ModelOrder {

    private String orderID;
    private String orderImage;
    private String orderName;
    private String orderStatus;
    private Double orderQuantity;
    private Double orderTotal;


    public ModelOrder() {
    }

    public ModelOrder(String orderID, String orderImage, String orderName, String orderStatus, Double orderQuantity, Double orderTotal) {
        this.orderID = orderID;
        this.orderImage = orderImage;
        this.orderName = orderName;
        this.orderStatus = orderStatus;
        this.orderQuantity = orderQuantity;
        this.orderTotal = orderTotal;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getOrderImage() {
        return orderImage;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public Double getOrderQuantity() {
        return orderQuantity;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }
}
