package com.example.demo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum OrderStatus {
    CREATED("Order Created", 1),
    PAID("Order Paid", 2),
    SHIPPED("Order Shipped", 3),
    COMPLETED("Order Completed", 4),
    CANCELED("Order Canceled", 5);

    private final String description;
    @EnumValue
    private final int code;

    OrderStatus() {
        this.description = this.name();
        this.code = this.ordinal();
    }

    OrderStatus(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public static OrderStatus getByDescription(String statusDesc) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.description.equals(statusDesc)) {
                return status;
            }
        }
        return null;
    }

    public OrderStatus getByCode(int code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public static int getCodeByDescription(String description) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.description.equals(description)) {
                return status.code;
            }
        }
        return -1;
    }
}
