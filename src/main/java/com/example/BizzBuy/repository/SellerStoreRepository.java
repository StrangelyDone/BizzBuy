package com.example.BizzBuy.repository;

import com.example.BizzBuy.model.SellerStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerStoreRepository extends MongoRepository<SellerStore, Long> {
    Optional<SellerStore> findByOwnerId(Long ownerId);
}
