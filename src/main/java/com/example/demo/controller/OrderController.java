package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.User;
import com.example.demo.request.OrderReq;
import com.example.demo.response.Result;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

//    @GetMapping("/list")
//    public Result listOrder(@RequestParam("userId") Long userId,@RequestParam("page") Long page,@RequestParam("pageSize") Long pageSize) {
//        log.info("Listing orders for userId: {}", userId);
//        if(userId == null){
//            log.error("UserId must not be null");
//            return Result.failure("UserId must not be null");
//        }
//        IPage<Order> res = orderService.queryOrdersByUserId(userId,page,pageSize);
//        return Result.success(res);
//    }

    @GetMapping("/list")
    public Result listOrder(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("Listing orders for userId: {}", userId);
        if(userId == null){
            log.error("UserId must not be null");
            return Result.failure("UserId must not be null");
        }
        Long page = request.getParameter("page") != null ? Long.parseLong(request.getParameter("page")) : 1L;
        Long pageSize = request.getParameter("pageSize") != null ? Long.parseLong(request.getParameter("pageSize")) : 10L;
        IPage<Order> res = orderService.queryOrdersByUserId(userId,page,pageSize);
        return Result.success(res);
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
