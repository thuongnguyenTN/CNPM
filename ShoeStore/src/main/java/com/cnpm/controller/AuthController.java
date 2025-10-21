package com.cnpm.controller;

import com.cnpm.entity.User;
import com.cnpm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
// Xóa "RedirectAttributes" nếu bạn không dùng nó ở đâu khác trong file này
// import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import jakarta.servlet.http.HttpSession; // THÊM IMPORT NÀY

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "public/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, 
                               BindingResult result, 
                               Model model, 
                               HttpSession session) { // <-- Dùng HttpSession
        
        // Kiểm tra xem username đã tồn tại chưa
        if (userService.findByUsername(user.getUsername()) != null) {
            result.rejectValue("username", "error.user", "Tên đăng nhập đã tồn tại.");
        }

        if (result.hasErrors()) {
            return "public/register";
        }

        // --- SỬA LỖI Ở ĐÂY ---
        // Gọi phương thức `saveNewUser` để đảm bảo `createdAt` được set giá trị
        userService.saveNewUser(user); 
        // THAY VÌ: userService.saveUser(user);
        
    
        // Thêm cờ vào session để hiển thị thông báo
        session.setAttribute("registerSuccess", true);

        return "redirect:/login"; // Chuyển hướng về trang đăng nhập
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) { // <-- Thêm Model và HttpSession
        
        // Kiểm tra cờ trong session
        if (session.getAttribute("registerSuccess") != null) {
            // Thêm thông báo vào Model
            model.addAttribute("registerSuccessMessage", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            
            // Xóa cờ khỏi session
            session.removeAttribute("registerSuccess");
        }
        
        return "public/login";
    }
    
    // Lưu ý: Spring Security tự động xử lý POST /login và /logout
}