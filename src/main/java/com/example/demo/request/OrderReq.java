package com.example.demo.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderReq {
    private String orderNo;
    private Long userId;
    private String userName;
    private Long productId;
    private Long quantity;
    private BigDecimal totalAmount;
    private String statusDesc;
}
