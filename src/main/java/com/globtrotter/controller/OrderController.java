package com.globtrotter.controller;

import com.globtrotter.model.Order;
import com.globtrotter.security.CustomUserDetails;
import com.globtrotter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestParam(required = false) String discountCode) {
        Order order = orderService.placeOrder(userDetails.getUser(), discountCode);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @PathVariable Integer orderId) {
        orderService.cancelOrder(orderId, userDetails.getUser());
        return ResponseEntity.ok().body(
                String.format("Order %d canceled and stock restored successfully.", orderId)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(orderService.getUserOrders(userDetails.getUser()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Integer orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
