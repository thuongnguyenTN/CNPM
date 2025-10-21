package com.cnpm.service;

import com.cnpm.entity.Coupon;
import com.cnpm.entity.Order;
import com.cnpm.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    // Chữ ký phương thức đã được cập nhật
    Order createOrder(User user, Optional<Coupon> coupon, String shippingAddress);

    Optional<Order> findById(Integer id);
    List<Order> findByUser(User user);
    List<Order> findAllOrders();
    void deleteOrder(Integer id);
    void cancelOrder(Integer orderId, User user);
    Order saveOrder(Order order);
    Map<String, Object> getMonthlyRevenueData();
}