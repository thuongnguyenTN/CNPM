package com.cnpm.controller;

import com.cnpm.entity.User;
import com.cnpm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Date;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private PasswordEncoder passwordEncoder;

    // Đường dẫn tĩnh để lưu ảnh đại diện
    private static final String AVATAR_UPLOAD_DIR = "src/main/resources/static/images/avatars/";

    private User getCurrentUser(Principal principal) {
        if (principal == null) return null;
        return userService.findByUsername(principal.getName());
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/profile";
    }

    // --- CẬP NHẬT PHƯƠNG THỨC NÀY ---
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user,
                                @RequestParam(value = "dob", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dob,
                                @RequestParam("avatarFile") MultipartFile avatarFile, // Thêm tham số này
                                Principal principal, RedirectAttributes redirectAttributes) {
        User existingUser = getCurrentUser(principal);
        if (existingUser == null) {
            return "redirect:/login";
        }

        // --- THÊM LOGIC XỬ LÝ UPLOAD ẢNH ---
        if (!avatarFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(AVATAR_UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                String fileName = existingUser.getUsername() + "_" + System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
                Path filePath = Paths.get(AVATAR_UPLOAD_DIR, fileName);
                Files.copy(avatarFile.getInputStream(), filePath);

                // Lưu đường dẫn web vào database
                existingUser.setAvatarUrl("/images/avatars/" + fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi tải lên ảnh đại diện.");
                return "redirect:/user/profile";
            }
        }

        // Cập nhật các thông tin khác
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setGender(user.getGender());
        existingUser.setDateOfBirth(dob);
        existingUser.setAddress(user.getAddress());

        userService.saveRawUser(existingUser);
        redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        return "redirect:/user/profile";
    }

    @PostMapping("/password/update")
    public String updatePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(principal);
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không chính xác.");
            return "redirect:/user/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "redirect:/user/profile";
        }

        userService.updatePassword(currentUser, newPassword);
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/user/profile";
    }
}