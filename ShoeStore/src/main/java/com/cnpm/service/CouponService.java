package com.cnpm.service;

import com.cnpm.entity.Coupon;
import com.cnpm.entity.User; // Thêm import
import java.util.List;
import java.util.Optional;

public interface CouponService {
    List<Coupon> findAll();
    Optional<Coupon> findById(Integer id);
    Coupon save(Coupon coupon);
    void deleteById(Integer id);
    
    // Cập nhật chữ ký phương thức để nhận thêm User
    Optional<Coupon> validateCoupon(String code, double orderTotal, User user);
    
    double applyCoupon(Coupon coupon, double orderTotal);
}
