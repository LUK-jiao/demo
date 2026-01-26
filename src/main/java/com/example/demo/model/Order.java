package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("orders")
public class Order {
    private Long id;

    private String orderNo;

    private Long userId;

    private Long productId;

    private Long quantity;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private Date createdAt;

    private Date updatedAt;
}