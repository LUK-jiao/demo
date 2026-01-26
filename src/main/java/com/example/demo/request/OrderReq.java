package com.example.demo.request;

import com.example.demo.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderReq {
    private Long userName;
    private Long productId;
    private Long quantity;
    private BigDecimal totalAmount;
    private String statusDesc;
}
