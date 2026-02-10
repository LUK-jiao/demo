package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.enums.ErrorCode;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.Order;
import com.example.demo.enums.OrderStatus;
import com.example.demo.model.OrderQuery;
import com.example.demo.request.OrderReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void updateOrder(OrderReq orderReq) {
        Order updateOrder = new Order();
        buildUpdateOrder(orderReq, updateOrder);
        try{
            orderMapper.updateById(updateOrder);
        }catch (Exception e){
            log.error("Failed to update order: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Order isOrderValid(OrderReq orderReq, Long userId) {
        OrderQuery orderQuery = new OrderQuery();
        orderQuery.setOrderNo(orderReq.getOrderNo());
        orderQuery.setUserId(userId);
        Order order = orderMapper.selectByUserIdAndOrderNo(orderQuery);
        return order;
    }

    private static void buildUpdateOrder(OrderReq orderReq, Order updateOrder) {
        updateOrder.setId(orderReq.getId());
        updateOrder.setOrderNo(orderReq.getOrderNo());
        updateOrder.setQuantity(orderReq.getQuantity());
        updateOrder.setTotalAmount(orderReq.getTotalAmount());
        updateOrder.setStatus(OrderStatus.getByDescription(orderReq.getStatusDesc()));
    }

    @Transactional
    public void deleteOrder(OrderReq orderReq) {
        try{
            orderMapper.deleteById(orderReq.getId());
        }catch (Exception e){
            log.error("Failed to delete order: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
