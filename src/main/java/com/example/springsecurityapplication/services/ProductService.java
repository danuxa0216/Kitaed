package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Category;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // получаем список всех товаров
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    // получаем товар по id
    public Product getProductId(int id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }

    @Transactional
    // сохраняем товар
    public void saveProduct(Product product, Category category) {
        product.setCategory(category);
        productRepository.save(product);
    }

    @Transactional
    // обновляем товар
    public void updateProduct(int id, Product product) {
        product.setId(id);
        productRepository.save(product);
    }

    @Transactional
    // удаляем товар
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}
