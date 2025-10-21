package com.cnpm.controller;

import com.cnpm.entity.Cart;
import com.cnpm.entity.Coupon;
import com.cnpm.entity.Order;
import com.cnpm.entity.User;
import com.cnpm.service.CartService;
import com.cnpm.service.CouponService;
import com.cnpm.service.OrderService;
import com.cnpm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@SessionAttributes("appliedCoupon")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;
    @Autowired private CartService cartService;
    @Autowired private CouponService couponService;

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        return userService.findByUsername(principal.getName());
    }
    
    @ModelAttribute("appliedCoupon")
    public Optional<Coupon> appliedCoupon() {
        return Optional.empty();
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal, 
                                 @ModelAttribute("appliedCoupon") Optional<Coupon> appliedCoupon) {
        User user = getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        
        Cart cart = cartService.getCartByUser(user);
        if (cart == null || cart.getCartItems().isEmpty()) {
            return "redirect:/cart";
        }

        double cartTotal = cartService.calculateCartTotal(cart);
        double discountAmount = 0.0;
        double finalTotal = cartTotal;

        if (appliedCoupon.isPresent()) {
            double discountedPrice = couponService.applyCoupon(appliedCoupon.get(), cartTotal);
            discountAmount = cartTotal - discountedPrice;
            finalTotal = discountedPrice;
        }

        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("finalTotal", finalTotal);
        model.addAttribute("user", user);
        
        return "public/checkout";
    }

    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam("code") String code, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        User user = getCurrentUser(principal); // Lấy user hiện tại
        Cart cart = cartService.getCartByUser(user);
        double cartTotal = cartService.calculateCartTotal(cart);

        // Truyền user vào hàm validate
        Optional<Coupon> couponOpt = couponService.validateCoupon(code, cartTotal, user);

        if (couponOpt.isPresent()) {
            model.addAttribute("appliedCoupon", couponOpt.get());
            redirectAttributes.addFlashAttribute("success", "Áp dụng mã giảm giá thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Mã giảm giá không hợp lệ, hết hạn hoặc đã được sử dụng.");
        }
        return "redirect:/orders/checkout";
    }

    @PostMapping("/place")
    public String placeOrder(Principal principal,
                           RedirectAttributes redirectAttributes,
                           @ModelAttribute("appliedCoupon") Optional<Coupon> appliedCoupon,
                           @RequestParam("address") String shippingAddress,
                           SessionStatus status) {
        User user = getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            orderService.createOrder(user, appliedCoupon, shippingAddress);
            status.setComplete();
            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công! Cảm ơn bạn đã mua sắm.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi đặt hàng: " + e.getMessage());
            return "redirect:/cart";
        }

        return "redirect:/orders/history";
    }

    @GetMapping("/history")
    public String orderHistory(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        List<Order> orders = orderService.findByUser(user);
        model.addAttribute("orders", orders);
        return "public/order-history";
    }
    
    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam("orderId") Integer orderId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            orderService.cancelOrder(orderId, user);
            redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng #" + orderId + " thành công.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/orders/history";
    }
}