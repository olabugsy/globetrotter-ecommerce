package com.globtrotter.service.impl;

import com.globtrotter.model.Product;
import com.globtrotter.repository.ProductRepository;
import com.globtrotter.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Integer productId, Product updatedProduct) {
        return productRepository.findById(productId)
                .map(existing -> {
                    existing.setName(updatedProduct.getName());
                    existing.setDescription(updatedProduct.getDescription());
                    existing.setPrice(updatedProduct.getPrice());
                    existing.setQuantity(updatedProduct.getQuantity());
                    existing.setImageUrl(updatedProduct.getImageUrl());
                    return productRepository.save(existing); // ✅ Save updated existing entity
                })
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
    }

    @Override
    public void deleteProduct(Integer productId) {
        productRepository.deleteById(productId);
    }
}
