package com.cnpm.service.impl;

import com.cnpm.entity.Product;
import com.cnpm.repository.OrderDetailRepository;
import com.cnpm.repository.ProductRepository;
import com.cnpm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> findByCategory(Integer categoryId) {
        return productRepository.findByCategoryCategoryId(categoryId);
    }
    // Phân trang
    @Override
    public Page<Product> findFilteredProducts(String keyword, Integer categoryId, Pageable pageable) {
        String searchKeyword = (keyword == null || keyword.isEmpty()) ? "" : keyword;
        
        if (categoryId != null && categoryId > 0) {
            // Lọc theo Category VÀ tìm kiếm
            return productRepository.findByCategoryCategoryIdAndNameContainingIgnoreCase(categoryId, searchKeyword, pageable);
        } else if (!searchKeyword.isEmpty()) {
            // Chỉ tìm kiếm theo tên
            return productRepository.findByNameContainingIgnoreCase(searchKeyword, pageable);
        } else {
            // Mặc định: Phân trang tất cả
            return productRepository.findAll(pageable);
        }
    }
    @Override
    public Page<Product> findPaginated(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    @Override
    public List<String> getSuggestionNames(String keyword) {
        // Phương thức này giờ đây hợp lệ vì nó gọi phương thức đã được khai báo ở trên
        return productRepository.findTop5ByNameContainingIgnoreCase(keyword).stream()
                .map(Product::getName)
                .collect(java.util.stream.Collectors.toList());
    }
    @Override
    public Map<String, Object> getTopSellingProductsData() {
        List<Map<String, Object>> results = orderDetailRepository.findTop5SellingProducts();
        Map<String, Object> data = new LinkedHashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        for (Map<String, Object> result : results) {
            labels.add((String) result.get("name"));
            quantities.add((Integer) result.get("totalQuantity"));
        }
        data.put("labels", labels);
        data.put("quantities", quantities);
        return data;
    }
    @Override
    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
}