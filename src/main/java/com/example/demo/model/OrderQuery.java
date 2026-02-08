package com.example.demo.model;

import com.example.demo.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderQuery {

    private Long id;

    private String orderNo;

    private Long userId;

    private Long productId;

    private Long quantity;

    private BigDecimal totalAmount;

    private OrderStatus status;
}
