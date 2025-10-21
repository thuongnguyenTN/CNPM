package com.cnpm.repository;

import com.cnpm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page; // Cần import
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryCategoryId(Integer categoryId);
 // 1. Tìm kiếm theo tên (phân trang)
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // 2. Lọc theo Category và Tìm kiếm theo tên (phân trang)
    Page<Product> findByCategoryCategoryIdAndNameContainingIgnoreCase(Integer categoryId, String name, Pageable pageable);
    
    // 3. Lọc theo Category (phân trang)
    Page<Product> findByCategoryCategoryId(Integer categoryId, Pageable pageable);
    
    List<Product> findTop5ByNameContainingIgnoreCase(String name);
    List<Product> findByNameContainingIgnoreCase(String name);
}