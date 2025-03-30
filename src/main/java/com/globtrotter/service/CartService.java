package com.globtrotter.service;

import com.globtrotter.model.CartItem;
import com.globtrotter.model.Product;
import com.globtrotter.model.User;
import java.util.List;

public interface CartService {
    CartItem addItemToCart(User user, Product product, int quantity);
    void updateCartItem(User user, Product product, int newQuantity);
    void removeItemFromCart(User user, Product product);
    void clearCart(User user);
    List<CartItem> getCartItems(User user);
}
