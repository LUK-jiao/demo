package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.User;
import com.example.demo.request.OrderReq;
import com.example.demo.response.Result;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping ("/order")
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public Result createOrder(@RequestBody OrderReq orderReq,@RequestAttribute("userId") Long userId) {
        log.info("Creating order for user: {}", orderReq);
        if(userId == null){
            return Result.failure("UserId must not be null");
        }
        orderReq.setUserId(userId);
        if(orderReq.getProductId() == null){
            return Result.failure("ProductId must not be null");
        }
        if(orderReq.getQuantity() == null || orderReq.getQuantity() <= 0) {
            return Result.failure("Quantity must be greater than 0");
        }
        if(orderReq.getTotalAmount() == null || orderReq.getTotalAmount().compareTo(new BigDecimal("0")) < 0 ) {
            return Result.failure("TotalAmount must be greater than 0");
        }
        Order order = new Order();
        String orderNo = generateOrderNo();
        setOrder(orderReq, order, orderNo);
        try{
            orderService.createOrder(order);
        }
        catch (Exception e){
            return Result.failure("Order creation failed");
        }
        return Result.success(orderNo);
    }

    @GetMapping("/list")
    public Result listOrder() {
        return Result.success(orderService.queryOrders());
    }

    private static void setOrder(OrderReq orderReq, Order order, String orderNo) {
        order.setOrderNo(orderNo);
        order.setUserId(orderReq.getUserId());
        order.setProductId(orderReq.getProductId());
        order.setQuantity(orderReq.getQuantity());
        order.setTotalAmount(orderReq.getTotalAmount());
        order.setStatus(OrderStatus.PAID);
    }

    private String generateOrderNo() {
        return System.currentTimeMillis() + "" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

}
