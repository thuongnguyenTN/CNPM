package com.cnpm;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.cnpm.entity.*;
import com.cnpm.service.UserService;
import com.cnpm.repository.*;

@SpringBootApplication
public class ShoeStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoeStoreApplication.class, args);
	}
	@Bean
	public CommandLineRunner initializeAdmin(UserService userService, BCryptPasswordEncoder passwordEncoder) {
	    return args -> {
	        if (userService.findByUsername("admin") == null) {
	            User admin = new User();
	            admin.setUsername("admin");
	            
	            // BƯỚC 1: Mã hóa mật khẩu gốc
	            String encodedPass = passwordEncoder.encode("admin"); 
	            admin.setPassword(encodedPass); 
	            
	            admin.setEmail("admin@shoestore.com");
	            admin.setRole("manager"); 
	            
	            // BƯỚC 2: SỬ DỤNG PHƯƠNG THỨC MỚI ĐỂ LƯU KHÔNG BỊ MÃ HÓA LẠI
	            userService.saveRawUser(admin); 
	            System.out.println("Tài khoản ADMIN đã được tạo.");
	        }
	    };
	}
	@Bean
	public CommandLineRunner initializeProductData(CategoryRepository categoryRepository, ProductRepository productRepository) {
	    return args -> {
	        // --- Đảm bảo tồn tại Categories ---
	    	if (categoryRepository.count() == 0) {
	            // SỬ DỤNG CONSTRUCTOR MỚI VÀ CUNG CẤP ĐƯỜNG DẪN ẢNH PLACEHOLDER
	            Category running = new Category("Giày Chạy Bộ", "/images/categories/running.png");
	            Category casual = new Category("Giày Thường Ngày", "/images/categories/casual.png");
	            
	            categoryRepository.save(running);
	            categoryRepository.save(casual);
	        }
	        // --- TẠO 15 SẢN PHẨM MẪU (Chỉ chạy nếu database trống) ---
	        if (productRepository.count() < 15) {
	            Category running = categoryRepository.findById(1).orElse(null); // Lấy Category ID 1
	            Category casual = categoryRepository.findById(2).orElse(null); // Lấy Category ID 2
	            
	            // Lặp để tạo nhanh 15 sản phẩm
	            for (int i = 1; i <= 15; i++) {
	                Product p = new Product();
	                p.setName("Giày Đa Năng Model " + i);
	                p.setDescription("Mô tả sản phẩm số " + i);
	                p.setPrice(1000000.0 + (i * 100000));
	                p.setStock(20);
	                p.setSize("40");
	                // Sử dụng đường dẫn ảnh mẫu (bạn cần tạo ảnh này trong static/images/)
	                p.setImageUrl("/images/shoe_model_" + (i % 3 + 1) + ".jpg"); 
	                p.setCategory(i % 2 == 0 ? running : casual);
	                p.setCreatedAt(new Date());
	                productRepository.save(p);
	            }
	            System.out.println(">>> Đã chèn thành công 15 sản phẩm mẫu.");
	        }
	    };
	}
}
