package com.example.BizzBuy.repository;

import com.example.BizzBuy.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, Long> {
    List<Transaction> findByPayerIdOrPayeeId(Long payerId, Long payeeId);
}
