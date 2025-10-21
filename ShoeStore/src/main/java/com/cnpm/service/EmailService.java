package com.cnpm.service;

public interface EmailService {
    /**
     * Gửi một email văn bản đơn giản.
     * @param to Địa chỉ email người nhận
     * @param subject Tiêu đề email
     * @param text Nội dung email
     */
    void sendSimpleMessage(String to, String subject, String text);
}