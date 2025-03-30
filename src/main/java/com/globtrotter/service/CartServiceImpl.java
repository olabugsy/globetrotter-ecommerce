package com.globtrotter.service.impl;

import com.globtrotter.model.Cart;
import com.globtrotter.model.CartItem;
import com.globtrotter.model.Product;
import com.globtrotter.model.User;
import com.globtrotter.repository.CartItemRepository;
import com.globtrotter.repository.CartRepository;
import com.globtrotter.repository.ProductRepository;
import com.globtrotter.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public CartItem addItemToCart(User user, Product product, int quantity) {
        Cart cart = getOrCreateCart(user);

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
        }

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        } else {
            int newTotal = cartItem.getQuantity() + quantity;
            if (product.getQuantity() < (newTotal - cartItem.getQuantity())) {
                throw new IllegalArgumentException("Not enough stock to update quantity for product: " + product.getName());
            }
            cartItem.setQuantity(newTotal);
        }
        cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cartItem.setAddedAt(LocalDateTime.now());
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    @Override
    public void updateCartItem(User user, Product product, int newQuantity) {
        Cart cart = getOrCreateCart(user);
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));
        cartItem.setQuantity(newQuantity);
        cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
        cartItem.setAddedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);
    }

    @Transactional
    @Override
    public void removeItemFromCart(User user, Product product) {
        Cart cart = getOrCreateCart(user);
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    @Override
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteAllByCart(cart);
    }

    @Override
    public List<CartItem> getCartItems(User user) {
        Cart cart = getOrCreateCart(user);
        return cartItemRepository.findByCart(cart);
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });
    }
}
