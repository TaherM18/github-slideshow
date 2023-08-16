package com.example.fimsdelivery.model;

public class Order {
    private String orderId;
    private String totalQuantity;
    private String itemQuantityName;
    private String orderPrice;
    private String orderDateTime;
    private String orderAddress;
    private String payMode;
    private String orderStatus;
    private String deliveryFee;

    private String customerName;
    private String customerPhone;

    public Order() {
    }

    public Order(String orderId, String totalQty, String orderPrice, String orderAddress, String payMode) {
        this.orderId = orderId;
        this.totalQuantity = totalQty;
        this.orderPrice = orderPrice;
        this.orderAddress = orderAddress;
        this.payMode = payMode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotalQuantity() {  return totalQuantity; }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getItemQuantityName() {
        return itemQuantityName;
    }

    public void setItemQuantityName(String itemQuantityName) {  this.itemQuantityName = itemQuantityName;   }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

}
