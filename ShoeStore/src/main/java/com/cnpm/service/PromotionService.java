package com.cnpm.service;

import com.cnpm.entity.Promotion;
import com.cnpm.entity.Product;
import java.util.List;
import java.util.Optional;

public interface PromotionService {
    List<Promotion> findAll();
    Optional<Promotion> findById(Integer id);
    Promotion save(Promotion promotion);
    void deleteById(Integer id);
    
    // Logic tính giá sau khuyến mãi
    double getDiscountedPrice(Product product);
}