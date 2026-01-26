package com.example.demo.model;

public enum OrderStatus {
    CREATED("Order Created", 1),
    PAID("Order Paid", 2),
    SHIPPED("Order Shipped", 3),
    COMPLETED("Order Completed", 4),
    CANCELED("Order Canceled", 5);

    private final String description;
    private final int code;

    OrderStatus() {
        this.description = this.name();
        this.code = this.ordinal();
    }

    OrderStatus(String description, int code) {
        this.description = description;
        this.code = code;
    }
}
