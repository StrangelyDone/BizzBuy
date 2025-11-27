package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Product;
import com.example.BizzBuy.util.IdGenerator;
import com.example.BizzBuy.util.JsonFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String PRODUCTS_FILE = "products.json";
    private final JsonFileManager fileManager;

    public Product createProduct(Product newProduct, Long sellerId) {
        List<Product> products = findAllInternal();
        Product product = Product.builder()
                .id(IdGenerator.nextId(products))
                .sellerId(sellerId)
                .name(newProduct.getName())
                .description(newProduct.getDescription())
                .images(newProduct.getImages())
                .price(newProduct.getPrice())
                .stockQuantity(newProduct.getStockQuantity())
                .tags(newProduct.getTags())
                .build();
        products.add(product);
        persist(products);
        return product;
    }

    public Product getProduct(Long productId) {
        return findAllInternal().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public List<Product> findBySeller(Long sellerId) {
        return findAllInternal().stream()
                .filter(p -> Objects.equals(p.getSellerId(), sellerId))
                .toList();
    }

    public List<Product> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return findAllInternal();
        }
        String query = keyword.toLowerCase(Locale.ROOT);
        return findAllInternal().stream()
                .filter(product -> (product.getName() != null
                        && product.getName().toLowerCase(Locale.ROOT).contains(query))
                        || (product.getDescription() != null
                                && product.getDescription().toLowerCase(Locale.ROOT).contains(query))
                        || (product.getTags() != null && product.getTags().stream()
                                .anyMatch(tag -> tag.toLowerCase(Locale.ROOT).contains(query))))
                .collect(Collectors.toList());
    }

    public List<Product> filter(Double min, Double max, Long sellerId) {
        return findAllInternal().stream()
                .filter(product -> min == null || product.getPrice() >= min)
                .filter(product -> max == null || product.getPrice() <= max)
                .filter(product -> sellerId == null || Objects.equals(product.getSellerId(), sellerId))
                .toList();
    }

    public Product updateStock(Long productId, Integer stock, Long sellerId) {
        List<Product> products = findAllInternal();
        Product product = products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (!product.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("Only owner can update stock");
        }
        product.setStockQuantity(stock);
        persist(products);
        return product;
    }

    public void decreaseStock(Long productId, int quantity) {
        List<Product> products = findAllInternal();
        Product product = products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        persist(products);
    }

    public List<Product> findAll() {
        return findAllInternal();
    }

    private List<Product> findAllInternal() {
        return new ArrayList<>(fileManager.readList(PRODUCTS_FILE, Product.class));
    }

    private void persist(List<Product> products) {
        fileManager.writeList(PRODUCTS_FILE, products);
    }
}
