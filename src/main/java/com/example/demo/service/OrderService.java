package com.example.demo.service;

import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    public void createOrder(Order order) {
        Order neworder = new Order();
        BeanUtils.copyProperties(order, neworder);
        try{
            orderMapper.insert(neworder);
        }catch (Exception e){
            throw new RuntimeException("Failed to create order");
        }

    }

    public List<Order> queryOrders(){
        return orderMapper.selectAll();
    }
}
