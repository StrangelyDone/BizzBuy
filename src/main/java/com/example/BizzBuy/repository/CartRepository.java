package com.example.BizzBuy.repository;

import com.example.BizzBuy.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
