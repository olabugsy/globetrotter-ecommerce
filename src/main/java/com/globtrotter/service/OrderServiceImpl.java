package com.globtrotter.service.impl;

import com.globtrotter.model.*;
import com.globtrotter.repository.*;
import com.globtrotter.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public Order placeOrder(User user, String discountCode) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found during checkout."));

            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItem.setCreatedAt(LocalDateTime.now());

            subtotal = subtotal.add(orderItem.getPrice());
            order.getOrderItems().add(orderItem);
        }

        BigDecimal discountAmount = applyDiscount(subtotal, discountCode);
        BigDecimal total = subtotal.subtract(discountAmount);

        order.setDiscountCode(discountCode);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAllByCart(cart);

        return savedOrder;
    }

    private BigDecimal applyDiscount(BigDecimal subtotal, String discountCode) {
        if (discountCode == null || discountCode.isBlank()) {
            return BigDecimal.ZERO;
        }

        return switch (discountCode.trim().toUpperCase()) {
            case "SAVE10" -> subtotal.multiply(BigDecimal.valueOf(0.10));
            case "SAVE20" -> subtotal.multiply(BigDecimal.valueOf(0.20));
            default -> BigDecimal.ZERO;
        };
    }

    @Transactional
    @Override
    public void cancelOrder(Integer orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to cancel this order.");
        }

        if (!order.getOrderStatus().equalsIgnoreCase("PLACED")) {
            throw new RuntimeException("Only placed orders can be canceled.");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found while canceling order."));
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        order.setOrderStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
