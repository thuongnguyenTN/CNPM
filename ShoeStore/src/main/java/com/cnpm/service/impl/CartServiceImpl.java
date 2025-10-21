package com.cnpm.service.impl;

import com.cnpm.entity.*;
import com.cnpm.repository.CartItemRepository;
import com.cnpm.repository.CartRepository;
import com.cnpm.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user);
    }

    @Override
    public void addProductToCart(User user, Product product, int quantity) {
        Cart cart = getCartByUser(user);
        if (cart == null) {
            // 1. Nếu giỏ hàng chưa tồn tại, tạo mới và lưu lại để lấy cartId
            cart = new Cart();
            cart.setUser(user);
            // LƯU VÀ CẬP NHẬT ĐỂ ĐẢM BẢO CÓ ID TỰ ĐỘNG
            cart = cartRepository.save(cart); 
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            // KHỞI TẠO COMPOSITE KEY TRƯỚC KHI GÁN
            CartItem newItem = new CartItem();
            
            // 1. Khởi tạo đối tượng khóa CartItemPK bằng ID của Cart và Product
            CartItemPK itemPK = new CartItemPK(cart.getCartId(), product.getProductId()); 
            
            // 2. Gán key đã khởi tạo
            newItem.setId(itemPK); 
            
            // 3. Gán các đối tượng Entity
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            
            cartItemRepository.save(newItem);
        }
    }
    // ... (các phương thức khác)
    public double calculateCartTotal(Cart cart) {
        if (cart == null || cart.getCartItems().isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (CartItem item : cart.getCartItems()) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    @Override
    public void updateProductQuantity(User user, Product product, int quantity) {
        Cart cart = getCartByUser(user);
        if (cart != null) {
            Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
            if (existingItem.isPresent()) {
                CartItem item = existingItem.get();
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            }
        }
    }

    @Override
    public void removeProductFromCart(User user, Product product) {
        Cart cart = getCartByUser(user);
        if (cart != null) {
            Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
            existingItem.ifPresent(cartItemRepository::delete);
        }
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getCartByUser(user);
        if (cart != null) {
            cartItemRepository.deleteAll(cart.getCartItems());
        }
    }
}