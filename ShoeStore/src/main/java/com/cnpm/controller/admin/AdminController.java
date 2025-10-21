package com.cnpm.controller.admin;

import com.cnpm.entity.Order;
import com.cnpm.entity.Product;
import com.cnpm.entity.User;
import com.cnpm.service.CategoryService;
import com.cnpm.service.OrderService;
import com.cnpm.service.ProductService;
import com.cnpm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private ProductService productService;
    @Autowired private UserService userService;
    @Autowired private OrderService orderService;
    @Autowired private CategoryService categoryService;

    private static final String UPLOAD_DIR = "uploads/images/products/";

    // [TRANG BÁO CÁO]
    @GetMapping({"", "/dashboard"})
    public String adminDashboard(Model model) {
        model.addAttribute("totalProducts", productService.findAllProducts().size());
        model.addAttribute("totalUsers", userService.findAllUsers().size());
        model.addAttribute("totalOrders", orderService.findAllOrders().size());
        
        // THÊM DỮ LIỆU BIỂU ĐỒ
        Map<String, Object> monthlyRevenueData = orderService.getMonthlyRevenueData();
        model.addAttribute("monthlyLabels", monthlyRevenueData.get("labels"));
        model.addAttribute("monthlyRevenues", monthlyRevenueData.get("revenues"));

        Map<String, Object> topProductsData = productService.getTopSellingProductsData();
        model.addAttribute("topProductLabels", topProductsData.get("labels"));
        model.addAttribute("topProductQuantities", topProductsData.get("quantities"));

        return "admin/dashboard";
    }

    // [QUẢN LÝ SẢN PHẨM]
    @GetMapping("/products")
    public String manageProducts(Model model, @RequestParam(name = "keyword", required = false) String keyword) {
        List<Product> productList;
        if (keyword != null && !keyword.isEmpty()) {
            productList = productService.searchByName(keyword);
            model.addAttribute("keyword", keyword); // Để giữ lại keyword trên ô tìm kiếm
        } else {
            productList = productService.findAllProducts();
        }
        model.addAttribute("products", productList);
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            if (product.getProductId() != null) {
                Product existingProduct = productService.findById(product.getProductId()).orElse(null);
                if (existingProduct != null && imageFile.isEmpty()) {
                    product.setImageUrl(existingProduct.getImageUrl());
                }
            }
            if (!imageFile.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.copy(imageFile.getInputStream(), filePath);
                
                // SỬA ĐƯỜNG DẪN LƯU VÀO DATABASE
                product.setImageUrl("/uploads/images/products/" + fileName);
            }
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Lưu sản phẩm thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi lưu file ảnh.");
            e.printStackTrace(); // In ra lỗi để dễ debug
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        return "redirect:/admin/products";
    }

    // [QUẢN LÝ NGƯỜI DÙNG]
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/updateRole")
    public String updateUserRole(@RequestParam("userId") Integer userId,
                                 @RequestParam("newRole") String newRole,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId).orElse(null);
        if (user != null) {
            user.setRole(newRole);
            userService.saveRawUser(user);
            redirectAttributes.addFlashAttribute("success", "Cập nhật vai trò thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
        }
        return "redirect:/admin/users";
    }
    
    // =================================================================
    // CÁC PHƯƠNG THỨC MỚI ĐƯỢC THÊM VÀO
    // =================================================================
    
    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        // Bạn cần tạo một file view tên là "user-form.html" trong thư mục "admin"
        // để làm form thêm và sửa thông tin người dùng.
        return "admin/user-form";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findById(id).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            // Tái sử dụng view "user-form.html" cho việc chỉnh sửa.
            return "admin/user-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }
    }
    
    // =================================================================

    // [QUẢN LÝ ĐƠN HÀNG]
    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", orderService.findAllOrders());
        return "admin/orders";
    }

    @PostMapping("/orders/updateStatus")
    public String updateOrderStatus(@RequestParam("orderId") Integer orderId,
                                    @RequestParam("status") String status,
                                    RedirectAttributes redirectAttributes) {
        Order order = orderService.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            // Tự động cập nhật trạng thái thanh toán nếu cần
            if ("completed".equals(status)) {
                order.setPaymentStatus("paid");
            } else if ("canceled".equals(status)) {
                 order.setPaymentStatus("refunded");
            }
            orderService.saveOrder(order); // Lưu thay đổi vào DB
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        }
        return "redirect:/admin/orders";
    }
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        User existingUser = userService.findById(user.getUserId()).orElse(null);
        if (existingUser != null) {
            existingUser.setFullName(user.getFullName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            existingUser.setRole(user.getRole());
            userService.saveRawUser(existingUser);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin người dùng thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
        }
        return "redirect:/admin/users";
    }
    @PostMapping("/users/reset-password")
    public String resetPassword(@RequestParam("userId") Integer userId,
                                @RequestParam("newPassword") String newPassword,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId).orElse(null);
        if (user != null) {
            userService.updatePassword(user, newPassword);
            redirectAttributes.addFlashAttribute("success", "Đặt lại mật khẩu cho người dùng '" + user.getUsername() + "' thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
        }
        return "redirect:/admin/users";
    }
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user,
                           @RequestParam(value = "password", required = false) String password,
                           RedirectAttributes redirectAttributes) {

        // TRƯỜNG HỢP 1: TẠO NGƯỜI DÙNG MỚI (vì userId chưa có)
        if (user.getUserId() == null) {
            // Kiểm tra xem username đã tồn tại chưa
            if (userService.findByUsername(user.getUsername()) != null) {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
                return "redirect:/admin/users/add";
            }
            // Gán mật khẩu và gọi service để lưu (service sẽ mã hóa mật khẩu)
            user.setPassword(password);
            userService.saveNewUser(user); // Giả định bạn có phương thức này trong UserService để mã hóa và lưu
            redirectAttributes.addFlashAttribute("success", "Thêm người dùng mới thành công!");

        // TRƯỜNG HỢP 2: CẬP NHẬT NGƯỜI DÙNG CŨ
        } else {
            User existingUser = userService.findById(user.getUserId()).orElse(null);
            if (existingUser != null) {
                existingUser.setFullName(user.getFullName());
                existingUser.setEmail(user.getEmail());
                existingUser.setPhone(user.getPhone());
                existingUser.setAddress(user.getAddress());
                existingUser.setRole(user.getRole());
                userService.saveRawUser(existingUser); // Không cập nhật mật khẩu ở đây
                redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin người dùng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            }
        }
        return "redirect:/admin/users";
    }
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa người dùng này. Có thể người dùng đã có đơn hàng hoặc dữ liệu liên quan.");
        }
        return "redirect:/admin/users";
    }
    
    
}