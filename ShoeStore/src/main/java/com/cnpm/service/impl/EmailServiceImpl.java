package com.cnpm.service.impl;

import com.cnpm.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    // Lấy tên email "từ" (username) từ application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage(); 
            message.setFrom(fromEmail);
            message.setTo(to); 
            message.setSubject(subject); 
            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            // Trong thực tế, bạn nên dùng Logger
            System.err.println("Lỗi khi gửi email đến " + to + ": " + e.getMessage());
            // Bạn có thể ném một ngoại lệ tùy chỉnh ở đây nếu cần
        }
    }
}