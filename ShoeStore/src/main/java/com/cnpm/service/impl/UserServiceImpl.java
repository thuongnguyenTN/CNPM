package com.cnpm.service.impl;

import com.cnpm.entity.User;
import com.cnpm.repository.UserRepository;
import com.cnpm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Override
    public User saveRawUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }
    @Override
    public User saveNewUser(User user) {
        // Gán vai trò mặc định là 'user' nếu chưa có
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }
        
        // SỬA Ở ĐÂY: Thêm dòng này để gán ngày tạo hiện tại
        user.setCreatedAt(new Date());

        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token).orElse(null);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        user.setResetPasswordToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // Token hết hạn sau 1 giờ
        userRepository.save(user);
    }

}