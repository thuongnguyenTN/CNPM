package com.cnpm.controller;

import com.cnpm.entity.User;
import com.cnpm.service.EmailService;
import com.cnpm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime; // <-- THÊM IMPORT
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
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
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "public/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, 
                                      RedirectAttributes redirectAttributes,
                                      Model model,
                                      HttpServletRequest request) { // <-- THÊM HttpServletRequest
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản với email này.");
            return "public/forgot-password";
        }

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        // --- BẮT ĐẦU PHẦN GỬI EMAIL ---
        
        // 1. Lấy URL gốc (ví dụ: http://localhost:8080)
        String appUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            appUrl += ":" + request.getServerPort();
        }
        
        // 2. Tạo link reset đầy đủ
        String resetLink = appUrl + "/reset-password?token=" + token;
        
        // 3. Tạo nội dung email
        String subject = "Yêu cầu đặt lại mật khẩu cho tài khoản ShoeStore";
        String body = "Xin chào " + user.getFullName() + " (username: " + user.getUsername() + "),\n\n"
                    + "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấp vào liên kết bên dưới để tiếp tục:\n"
                    + resetLink + "\n\n"
                    + "Nếu bạn không yêu cầu, vui lòng bỏ qua email này. Liên kết sẽ hết hạn sau 1 giờ.\n\n"
                    + "Trân trọng,\nĐội ngũ ShoeStore.";
        
        // 4. Gửi email
        emailService.sendSimpleMessage(user.getEmail(), subject, body);

        // --- KẾT THÚC PHẦN GỬI EMAIL ---

        // Thay đổi thông báo cho người dùng
        redirectAttributes.addFlashAttribute("success", "Yêu cầu thành công. Vui lòng kiểm tra email (" + email + ") để đặt lại mật khẩu.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        User user = userService.findByResetPasswordToken(token);
        if (user == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "public/login";
        }
        model.addAttribute("token", token);
        return "public/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        
        model.addAttribute("token", token); 

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "public/reset-password";
        }

        User user = userService.findByResetPasswordToken(token);
        if (user == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "redirect:/login";
        }

        userService.updatePassword(user, password); // Phương thức này đã mã hóa mật khẩu
        
        // Xóa token sau khi đã dùng
        user.setResetPasswordToken(null);
        user.setResetTokenExpiry(null);
        userService.saveRawUser(user); // Lưu lại user mà không mã hóa lại mật khẩu

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }
}