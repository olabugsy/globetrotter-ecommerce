package com.globtrotter.controller;

import com.globtrotter.model.CartItem;
import com.globtrotter.model.Product;
import com.globtrotter.security.CustomUserDetails;
import com.globtrotter.service.CartService;
import com.globtrotter.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @RequestParam Integer productId,
                                       @RequestParam int quantity) {
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOpt.get();
        CartItem cartItem = cartService.addItemToCart(userDetails.getUser(), product, quantity);

        Product updatedProduct = productService.getProductById(productId).orElseThrow();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item added to cart successfully.");
        response.put("cartItem", cartItem);
        response.put("updatedProductQuantity", updatedProduct.getQuantity());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestParam Integer productId,
                                            @RequestParam int quantity) {
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOpt.get();
        cartService.updateCartItem(userDetails.getUser(), product, quantity);

        Product updatedProduct = productService.getProductById(productId).orElseThrow();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cart item updated.");
        response.put("updatedProductQuantity", updatedProduct.getQuantity());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestParam Integer productId) {
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        cartService.removeItemFromCart(userDetails.getUser(), productOpt.get());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item removed from cart.");
        response.put("restoredProductQuantity", productService.getProductById(productId).get().getQuantity());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getUser());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cart cleared and product quantities restored.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> viewCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CartItem> items = cartService.getCartItems(userDetails.getUser());
        return ResponseEntity.ok(items);
    }
}
