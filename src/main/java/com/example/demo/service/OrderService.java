package com.example.demo.service;

import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Transactional
    public void createOrder(Order order) {
        Order neworder = new Order();
        BeanUtils.copyProperties(order, neworder);
        try{
            orderMapper.insert(neworder);
        }catch (Exception e){
            log.error("Failed to create order: {}", e.getMessage());
            throw new RuntimeException("Failed to create order");
        }
    }

    public Order queryOrders(){
        return orderMapper.selectById("1");
    }
}
