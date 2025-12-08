package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Transaction;
import com.example.BizzBuy.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final SequenceGeneratorService sequenceGenerator;

    public Transaction log(Long payerId, Long payeeId, double amount, String reference) {
        Transaction transaction = Transaction.successful(payerId, payeeId, amount, reference);
        transaction.setId(sequenceGenerator.generateSequence("transactions_sequence"));
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);

        return transactionRepository.save(transaction);
    }

    public List<Transaction> findByUser(Long userId) {
        return transactionRepository.findByPayerIdOrPayeeId(userId, userId);
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }
}
