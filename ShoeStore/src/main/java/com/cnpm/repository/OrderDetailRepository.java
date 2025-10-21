package com.cnpm.repository;


import com.cnpm.entity.Order;
import com.cnpm.entity.OrderDetail;
import com.cnpm.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Map;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    List<OrderDetail> findByOrder(Order order);
    @Query(value = "SELECT TOP 5 p.name, SUM(od.quantity) as totalQuantity " +
            "FROM order_details od " +
            "JOIN products p ON od.product_id = p.product_id " +
            "GROUP BY p.name " +
            "ORDER BY totalQuantity DESC", nativeQuery = true)
List<Map<String, Object>> findTop5SellingProducts();
}