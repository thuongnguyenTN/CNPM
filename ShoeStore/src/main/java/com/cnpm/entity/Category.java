package com.cnpm.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    // THÊM TRƯỜNG IMAGE VÀ ÁNH XẠ CHÍNH XÁC VỚI CỘT BẮT BUỘC 'image'
    @Column(name = "image", nullable = false) 
    private String imageUrl; // Sử dụng tên trường này để dễ quản lý

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
    
    // 1. Hàm tạo rỗng (bắt buộc cho JPA)
    public Category() {}
    
    // 2. CẬP NHẬT CONSTRUCTOR BỊ THIẾU (Nếu bạn muốn dùng nó)
    public Category(String categoryName) {
        this.categoryName = categoryName;
        // Cần gán giá trị mặc định nếu hàm tạo này được dùng cho cột NOT NULL
        this.imageUrl = "/images/default_category.png"; 
    }
    
    // 3. HÀM TẠO MỚI (PHẢI CÓ): Chấp nhận cả tên và URL ảnh
    public Category(String categoryName, String imageUrl) {
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters (Đảm bảo có getter/setter cho imageUrl)
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}