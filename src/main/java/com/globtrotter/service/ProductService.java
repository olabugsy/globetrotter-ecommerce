package com.globtrotter.service;

import com.globtrotter.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> getProductById(Integer productId);

    Product createProduct(Product product);

    Product updateProduct(Integer productId, Product product);

    void deleteProduct(Integer productId);
}
