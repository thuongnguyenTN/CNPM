package com.cnpm.service.impl;

import com.cnpm.entity.Coupon;
import com.cnpm.entity.User;
import com.cnpm.repository.CouponRepository;
import com.cnpm.repository.OrderRepository;
import com.cnpm.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired private OrderRepository orderRepository;

    @Override
    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    @Override
    public Optional<Coupon> findById(Integer id) {
        return couponRepository.findById(id);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public void deleteById(Integer id) {
        couponRepository.deleteById(id);
    }

    @Override
    public Optional<Coupon> validateCoupon(String code, double orderTotal, User user) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(code);

        if (couponOpt.isEmpty()) {
            return Optional.empty(); // Mã không tồn tại
        }

        Coupon coupon = couponOpt.get();
        Date now = new Date();

        // 1. Kiểm tra ngày hết hạn
        if (coupon.getEndDate().before(now)) {
            return Optional.empty();
        }

        // 2. Kiểm tra giá trị đơn hàng tối thiểu
        if (orderTotal < coupon.getMinOrderValue()) {
            return Optional.empty();
        }

        // 3. KIỂM TRA XEM NGƯỜI DÙNG ĐÃ SỬ DỤNG MÃ NÀY CHƯA
        if (orderRepository.existsByUserAndAppliedCoupon(user, coupon)) {
            return Optional.empty(); // Đã sử dụng
        }

        return couponOpt; // Hợp lệ
    }
    
    @Override
    public double applyCoupon(Coupon coupon, double orderTotal) {
        // SỬA TỪ coupon.getType() sang coupon.getDiscountType()
        if (coupon.getDiscountType().equalsIgnoreCase("percent")) {
            double discount = orderTotal * (coupon.getDiscountValue() / 100);
            return orderTotal - discount;
        }
        // SỬA TỪ coupon.getType() sang coupon.getDiscountType()
        else if (coupon.getDiscountType().equalsIgnoreCase("amount")) {
            return orderTotal - coupon.getDiscountValue();
        }
        return orderTotal;
    }
}