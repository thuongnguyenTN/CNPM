package com.cnpm.service;

import com.cnpm.entity.Cart;
import com.cnpm.entity.Product;
import com.cnpm.entity.User;

public interface CartService {
    Cart getCartByUser(User user);
    void addProductToCart(User user, Product product, int quantity);
    void updateProductQuantity(User user, Product product, int quantity);
    void removeProductFromCart(User user, Product product);
    void clearCart(User user);
    double calculateCartTotal(Cart cart);
}