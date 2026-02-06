package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.model.Order;
import com.example.demo.enums.OrderStatus;
import com.example.demo.request.OrderReq;
import com.example.demo.response.Result;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PostMapping("update")
    public Result updateOrder(OrderReq orderReq,@RequestAttribute("userId") Long userId) {
        //更新订单信息接口，可以修改订单。首先校验订单号是否属于对应的userId，然后修改订单。这些逻辑都在serviece
        log.info("updating Order req:{},userId:{}", JSON.toJSONString(orderReq),userId);
        if(userId == null){
            log.error("UserId must not be null");
            return Result.failure("UserId must not be null");
        }
        if (orderReq == null) {
            log.warn("Update order request is empty");
            return Result.failure("orderReq must not be null");
        }
        try{
            orderService.updateOrder(orderReq,userId);
            return Result.successWithMsg(String.format("Order updated successfully %s", orderReq.getOrderNo()));
        }catch(Exception e){
            log.error("Failed to update order: {}", e.getMessage());
            return Result.failure("Failed to update order");
        }
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
