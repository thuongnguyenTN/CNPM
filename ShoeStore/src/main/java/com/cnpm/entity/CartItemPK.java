package com.cnpm.entity;


import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CartItemPK implements Serializable {
    private Integer cartId;
    private Integer productId;

    // Constructors, Getters, Setters, hashCode, and equals
    public CartItemPK() {}
    public CartItemPK(Integer cartId, Integer productId) {
        this.cartId = cartId;
        this.productId = productId;
    }

    public Integer getCartId() { return cartId; }
    public void setCartId(Integer cartId) { this.cartId = cartId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemPK that = (CartItemPK) o;
        return Objects.equals(cartId, that.cartId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, productId);
    }
}