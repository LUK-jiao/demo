package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    public IPage<Order> queryOrdersByUserId(Long userId,Long pageNum,Long pageSize) {
        log.info("queryOrdersByUserId:userId=={},page=={},pageSize=={}",userId,pageNum,pageSize);
        Page<Order> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        IPage<Order> res;
        try{
            res = orderMapper.selectPage(page, queryWrapper);

        } catch (Exception e) {
            log.error("Failed to query orders by userId: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Queried {} orders for userId {}", res.getTotal(),userId);
        return res;
    }
}
