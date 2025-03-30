package com.globtrotter.service;

import com.globtrotter.model.Order;
import com.globtrotter.model.User;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order placeOrder(User user, String discountCode);

    void cancelOrder(Integer orderId, User user);

    List<Order> getUserOrders(User user);

    Optional<Order> getOrderById(Integer orderId);

    List<Order> getAllOrders(); // ADMIN ONLY
}
