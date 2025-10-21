package com.cnpm.controller;

import com.cnpm.entity.Product;
import com.cnpm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired private CategoryService categoryService;
    
    @GetMapping({"/", "/products"})
    public String listProducts(Model model, 
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "12") int size,
                               @RequestParam(value = "keyword", required = false) String keyword, // THÊM
                               @RequestParam(value = "categoryId", required = false) Integer categoryId) { // THÊM
        
        Pageable pageable = PageRequest.of(page, size);

        // Gọi Service với các tham số lọc mới
        Page<Product> productPage = productService.findFilteredProducts(keyword, categoryId, pageable);
        
        // Truyền dữ liệu phân trang và lọc vào Model
        model.addAttribute("productPage", productPage); 
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("title", "Tất cả Sản phẩm Giày");
        
        // Truyền lại trạng thái hiện tại để duy trì trên View
        model.addAttribute("currentKeyword", keyword);
        model.addAttribute("currentCategoryId", categoryId);
        
        return "public/product-list";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            // Xử lý lỗi: không tìm thấy sản phẩm
            return "redirect:/products";
        }
        model.addAttribute("product", product.get());
        return "public/product-detail";
    }
    @GetMapping("/api/products/suggestions")
    @ResponseBody
    public List<String> getProductSuggestions(@RequestParam("query") String query) {
        if (query == null || query.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return productService.getSuggestionNames(query);
    }
}