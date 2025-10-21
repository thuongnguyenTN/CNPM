package com.cnpm.entity;


import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "size")
    private String size;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "ProductPromotions", // Tên bảng nối trong CSDL
        joinColumns = @JoinColumn(name = "product_id"), // Khóa chính của Product
        inverseJoinColumns = @JoinColumn(name = "promo_id") // Khóa ngoại tới Promotion
    )
    private List<Promotion> promotions;

    // Getters and Setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public List<Promotion> getPromotions() { return promotions; }
    public void setPromotions(List<Promotion> promotions) { this.promotions = promotions; }
}