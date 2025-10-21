package com.cnpm.service.impl;

import com.cnpm.entity.*;
import com.cnpm.repository.OrderRepository;
import com.cnpm.repository.ProductRepository;
import com.cnpm.service.CartService;
import com.cnpm.service.CouponService;
import com.cnpm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartService cartService;
    @Autowired private CouponService couponService;

    @Override
    @Transactional
    public Order createOrder(User user, Optional<Coupon> coupon, String shippingAddress) {
        Cart cart = cartService.getCartByUser(user);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể tạo đơn hàng.");
        }

        // 1. Khởi tạo đối tượng Order và các thuộc tính
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setStatus("pending");
        newOrder.setCreatedAt(new Date());
        newOrder.setShippingAddress(shippingAddress);
        newOrder.setPaymentMethod("COD");
        newOrder.setPaymentStatus("unpaid");
        newOrder.setOrderDetails(new ArrayList<>()); // Rất quan trọng: khởi tạo danh sách
        coupon.ifPresent(newOrder::setAppliedCoupon);
        double totalAmount = 0.0;

        // 2. Lặp qua các sản phẩm trong giỏ hàng để tạo OrderDetail
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();

            if (product.getStock() < quantity) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho.");
            }

            OrderDetail detail = new OrderDetail();
            
            // === PHẦN SỬA LỖI QUAN TRỌNG NHẤT ===
            // KHÔNG cần tạo new OrderDetailId() nữa.
            // Chỉ cần thiết lập mối quan hệ, JPA sẽ tự động xử lý khóa chính phức hợp.
            detail.setOrder(newOrder); 
            detail.setProduct(product);
            
            detail.setQuantity(quantity);
            detail.setPrice(product.getPrice());
            detail.setId(new OrderDetailId(
            	    newOrder.getOrderId(),  // có thể null lúc đầu, nhưng Hibernate sẽ xử lý nhờ @MapsId
            	    product.getProductId()
            	));

            // Thêm chi tiết vào danh sách của đối tượng Order
            newOrder.getOrderDetails().add(detail);

            // Cập nhật tồn kho
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            totalAmount += product.getPrice() * quantity;
        }

        // 3. Áp dụng mã giảm giá (nếu có)
        if (coupon.isPresent()) {
            totalAmount = couponService.applyCoupon(coupon.get(), totalAmount);
        }

        // 4. Gán tổng số tiền cuối cùng
        newOrder.setTotalAmount(totalAmount);
        
        // 5. Lưu Order (JPA sẽ tự động lưu các OrderDetail liên quan nhờ CascadeType.ALL)
        Order savedOrder = orderRepository.save(newOrder);

        // 6. Xóa giỏ hàng
        cartService.clearCart(user);
        
        return savedOrder;
    }
    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
    @Override
    @Transactional
    public void cancelOrder(Integer orderId, User user) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng."));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này.");
        }

        if (!"pending".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Không thể hủy đơn hàng đã được xử lý hoặc đã hủy.");
        }

        order.setStatus("canceled");
        orderRepository.save(order);

        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        }
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findByUser(User user) {
        return orderRepository.findAllByUserWithDetails(user);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }
    @Override
    public Map<String, Object> getMonthlyRevenueData() {
        List<Map<String, Object>> results = orderRepository.findMonthlyRevenueLast6Months();
        Map<String, Object> data = new LinkedHashMap<>();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();
        for (Map<String, Object> result : results) {
            labels.add((String) result.get("month"));
            revenues.add((BigDecimal) result.get("revenue"));
        }
        data.put("labels", labels);
        data.put("revenues", revenues);
        return data;
    }
    
}