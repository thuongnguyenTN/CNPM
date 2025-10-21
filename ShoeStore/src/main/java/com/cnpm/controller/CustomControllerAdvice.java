package com.cnpm.controller;

import com.cnpm.entity.Cart;
import com.cnpm.entity.User;
import com.cnpm.service.CartService;
import com.cnpm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class CustomControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @ModelAttribute("cartItemCount")
    public int getCartItemCount(Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            Cart cart = cartService.getCartByUser(user);
            if (cart != null && cart.getCartItems() != null) {
                return cart.getCartItems().size();
            }
        }
        return 0; // Trả về 0 nếu người dùng chưa đăng nhập hoặc giỏ hàng trống
    }
}