package com.example.BizzBuy.repository;

import com.example.BizzBuy.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);
}
