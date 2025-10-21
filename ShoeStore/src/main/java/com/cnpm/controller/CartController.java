package com.cnpm.controller;

import com.cnpm.entity.*;
import com.cnpm.service.CartService;
import com.cnpm.service.ProductService;
import com.cnpm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // Lấy User hiện tại (cần cấu hình chi tiết hơn trong Security)
    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName());
    }

    @GetMapping("/cart")
    public String showCart(Model model, Principal principal) {
        if (principal == null) { return "redirect:/login"; }
        User user = getCurrentUser(principal);
        Cart cart = cartService.getCartByUser(user);
        
        model.addAttribute("cart", cart);
        // Tính tổng tiền để hiển thị trên View
        model.addAttribute("cartTotal", cartService.calculateCartTotal(cart)); 
        return "public/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Integer productId, 
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity, 
                            Principal principal, 
                            RedirectAttributes redirectAttributes) { // THÊM RedirectAttributes
        if (principal == null) {
            return "redirect:/login";
        }
        User user = getCurrentUser(principal);
        Product product = productService.findById(productId).orElse(null);
        
        if (product != null) {
            cartService.addProductToCart(user, product, quantity);
            // THÔNG BÁO THÀNH CÔNG THÊM VÀO GIỎ HÀNG
            redirectAttributes.addFlashAttribute("success", "Đã thêm " + quantity + " sản phẩm " + product.getName() + " vào giỏ hàng thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Sản phẩm không tồn tại.");
        }
        return "redirect:/products/" + productId;
    }

    // [CHỨC NĂNG 2]: XÓA KHỎI GIỎ HÀNG
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("productId") Integer productId,     
                                 Principal principal, 
                                 RedirectAttributes redirectAttributes) { // THÊM RedirectAttributes
        if (principal == null) {
            return "redirect:/login";
        }
        User user = getCurrentUser(principal);
        Product product = productService.findById(productId).orElse(null);
        
        if (product != null) {
            cartService.removeProductFromCart(user, product);
            // THÔNG BÁO THÀNH CÔNG XÓA
            redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm " + product.getName() + " khỏi giỏ hàng.");
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam("productId") Integer productId, @RequestParam("quantity") int quantity, Principal principal) {
        if (principal == null) { return "redirect:/login"; }
        User user = getCurrentUser(principal);
        Product product = productService.findById(productId).orElse(null);

        if (product != null) {
            cartService.updateProductQuantity(user, product, quantity);
        }
        return "redirect:/cart";
    }

    
}