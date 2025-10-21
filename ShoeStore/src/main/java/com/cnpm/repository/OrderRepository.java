package com.cnpm.repository;

import com.cnpm.entity.Coupon;
import com.cnpm.entity.Order;
import com.cnpm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	@Query("SELECT o FROM Order o " + "LEFT JOIN FETCH o.orderDetails od " + "LEFT JOIN FETCH od.product "
			+ "WHERE o.user = :user " + "ORDER BY o.createdAt DESC")
	List<Order> findAllByUserWithDetails(User user);

	boolean existsByUserAndAppliedCoupon(User user, Coupon coupon);

	@Query(value = "SELECT FORMAT(o.created_at, 'yyyy-MM') as month, SUM(o.total_amount) as revenue " + "FROM orders o "
			+ "WHERE o.status = 'completed' AND o.created_at >= DATEADD(month, -6, GETDATE()) "
			+ "GROUP BY FORMAT(o.created_at, 'yyyy-MM') " + "ORDER BY month", nativeQuery = true)
	List<Map<String, Object>> findMonthlyRevenueLast6Months();
}