package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Product;
import com.example.BizzBuy.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//automatically creates an instance of this class..! :O
@Service
public class ProductService {
    // save the path in a string..!
    private final ProductRepository productRepository;
    private final SequenceGeneratorService sequenceGenerator;

    // this is directly used by springboot to instantiate this service class..!
    public ProductService(ProductRepository productRepository, SequenceGeneratorService sequenceGenerator) {
        this.productRepository = productRepository;
        this.sequenceGenerator = sequenceGenerator;
    }

    public Product createProduct(Product newProduct, Long sellerId) {
        List<Product> products = productRepository.findAll();
        for (Product existingProduct : products) {
            boolean isSameProduct = Objects.equals(existingProduct.getSellerId(), sellerId) &&
                    Objects.equals(existingProduct.getName(), newProduct.getName()) &&
                    Objects.equals(existingProduct.getDescription(), newProduct.getDescription()) &&
                    Objects.equals(existingProduct.getPrice(), newProduct.getPrice()) &&
                    Objects.equals(existingProduct.getImages(), newProduct.getImages()) &&
                    Objects.equals(existingProduct.getTags(), newProduct.getTags());

            if (isSameProduct) {
                existingProduct.setStockQuantity(existingProduct.getStockQuantity() + newProduct.getStockQuantity());
                return productRepository.save(existingProduct);
            }
        }

        Product product = Product.builder()
                .id(sequenceGenerator.generateSequence("products_sequence"))
                .sellerId(sellerId)
                .name(newProduct.getName())
                .description(newProduct.getDescription())
                .images(newProduct.getImages())
                .price(newProduct.getPrice())
                .stockQuantity(newProduct.getStockQuantity())
                .tags(newProduct.getTags())
                .build();

        return productRepository.save(product);
    }

    public List<Product> fetchProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> findBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public List<Product> search(String keyword) {
        // if theres no text in the keyword... i.e its empty, then show all the
        // products..!
        if (!StringUtils.hasText(keyword))
            return fetchProducts();

        String query = keyword.toLowerCase();
        List<Product> products = fetchProducts();
        List<Product> finalList = new ArrayList<>();

        for (Product p : products) {
            boolean flag = false;

            // check in the name, description and tags..!
            if (p.getName() != null && p.getName().toLowerCase().contains(query))
                flag = true;
            else if (p.getDescription() != null && p.getDescription().toLowerCase().contains(query))
                flag = true;
            else if (p.getTags() != null) {
                List<String> tags = p.getTags();
                for (String tag : tags) {
                    if (tag != null && tag.toLowerCase().contains(query)) {
                        flag = true;
                        break;
                    }
                }
            }

            if (flag)
                finalList.add(p);

        }

        return finalList;
    }

    public List<Product> filter(Double min, Double max, Long sellerId) {
        List<Product> allProducts = fetchProducts();
        List<Product> result = new ArrayList<>();

        for (Product p : allProducts) {
            // if the conditions fail,then check the next product..!
            if (min != null && p.getPrice() < min)
                continue;

            if (max != null && p.getPrice() > max)
                continue;

            if (sellerId != null && !Objects.equals(p.getSellerId(), sellerId))
                continue;

            result.add(p);
        }
        return result;
    }

    public Product updateStock(Long productId, Integer stock, Long sellerId) {
        Product p = getProduct(productId);

        if (!p.getSellerId().equals(sellerId))
            throw new IllegalArgumentException("Only owner can update stock");

        // p is just a reference to the corresponding product object, so
        // just changing it directly is fine..!
        p.setStockQuantity(stock);
        return productRepository.save(p);
    }

    public void decreaseStock(Long productId, int quantity) {
        if (productId == null)
            throw new IllegalArgumentException("Product ID cannot be null");

        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity to decrease must be greater than 0");

        Product p = getProduct(productId);

        if (p.getStockQuantity() < quantity)
            throw new IllegalArgumentException("Insufficient stock. Available: " + p.getStockQuantity());

        p.setStockQuantity(p.getStockQuantity() - quantity);
        productRepository.save(p);
    }
}
