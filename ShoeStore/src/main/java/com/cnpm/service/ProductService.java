package com.cnpm.service;

import com.cnpm.entity.Product;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    List<Product> findAllProducts();
    Optional<Product> findById(Integer id);
    Product saveProduct(Product product);
    void deleteProduct(Integer id);
    List<Product> findByCategory(Integer categoryId);
    Page<Product> findPaginated(Pageable pageable);
    Page<Product> findFilteredProducts(String keyword, Integer categoryId, Pageable pageable);
    List<String> getSuggestionNames(String keyword);
    Map<String, Object> getTopSellingProductsData();
    List<Product> searchByName(String keyword);
}