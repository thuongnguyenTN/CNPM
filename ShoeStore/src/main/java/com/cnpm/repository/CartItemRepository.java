package com.cnpm.repository;

import com.cnpm.entity.Cart;
import com.cnpm.entity.CartItem;
import com.cnpm.entity.CartItemPK;
import com.cnpm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemPK> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}