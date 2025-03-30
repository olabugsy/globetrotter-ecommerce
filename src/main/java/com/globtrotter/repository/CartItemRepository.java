package com.globtrotter.repository;

import com.globtrotter.model.Cart;
import com.globtrotter.model.CartItem;
import com.globtrotter.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    void deleteByCart(Cart cart);

    void deleteAllByCart(Cart cart);
}
