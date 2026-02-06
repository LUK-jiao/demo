package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.enums.ErrorCode;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.Order;
import com.example.demo.enums.OrderStatus;
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
    public void updateOrder(OrderReq orderReq, Long userId) {

        Order order = orderMapper.selectByUserIdAndOrderNo(
                userId, orderReq.getOrderNo());
        if(order == null){
            log.info("Failed to query order by orderNo: {} ,userId:{}", orderReq.getOrderNo(),userId);
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        Order updateOrder = new Order();
        buildUpdateOrder(orderReq, updateOrder, order);
        try{
            orderMapper.updateById(updateOrder);
        }catch (Exception e){
            log.error("Failed to update order: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void buildUpdateOrder(OrderReq orderReq, Order updateOrder, Order order) {
        updateOrder.setId(order.getId());
        updateOrder.setOrderNo(orderReq.getOrderNo());
        updateOrder.setQuantity(orderReq.getQuantity());
        updateOrder.setTotalAmount(orderReq.getTotalAmount());
        updateOrder.setStatus(OrderStatus.getByDescription(orderReq.getStatusDesc()));
    }
}
