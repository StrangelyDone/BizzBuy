package com.example.BizzBuy.repository;

import com.example.BizzBuy.model.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends MongoRepository<Wallet, Long> {
}
