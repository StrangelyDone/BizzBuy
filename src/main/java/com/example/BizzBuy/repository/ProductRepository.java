package com.example.BizzBuy.repository;

import com.example.BizzBuy.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query("{ 'price' : { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceRange(Double min, Double max);

    List<Product> findBySellerId(Long sellerId);
}
