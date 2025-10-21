package com.cnpm.service.impl;

import com.cnpm.entity.Promotion;
import com.cnpm.entity.Product;
import com.cnpm.repository.PromotionRepository;
import com.cnpm.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public List<Promotion> findAll() {
        return promotionRepository.findAll();
    }

    @Override
    public Optional<Promotion> findById(Integer id) {
        return promotionRepository.findById(id);
    }

    @Override
    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public void deleteById(Integer id) {
        promotionRepository.deleteById(id);
    }

    @Override
    public double getDiscountedPrice(Product product) {
        // TODO: Triển khai logic phức tạp để tìm khuyến mãi hợp lệ cho sản phẩm
        
        // Hiện tại, chỉ tìm khuyến mãi đang hoạt động (ví dụ đơn giản)
        Date now = new Date();
        List<Promotion> activePromotions = promotionRepository.findByStartDateBeforeAndEndDateAfter(now, now);
        
        // Ví dụ: Áp dụng khuyến mãi đầu tiên tìm thấy
        if (!activePromotions.isEmpty()) {
            Promotion promotion = activePromotions.get(0);
            double originalPrice = product.getPrice();
            
            if (promotion.getDiscountType().equals("PERCENT")) {
                return originalPrice * (1 - promotion.getDiscountValue() / 100);
            } else if (promotion.getDiscountType().equals("FIXED")) {
                return originalPrice - promotion.getDiscountValue();
            }
        }
        
        return product.getPrice(); // Không có khuyến mãi
    }
}