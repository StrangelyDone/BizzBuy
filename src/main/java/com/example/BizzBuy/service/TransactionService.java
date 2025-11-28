package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Transaction;
import com.example.BizzBuy.util.IdGenerator;
import com.example.BizzBuy.util.JsonFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final String TRANSACTIONS_FILE = "transactions.json";
    private final JsonFileManager fileManager;

    public Transaction log(Long payerId, Long payeeId, double amount, String reference) {
        List<Transaction> transactions = new ArrayList<>(fileManager.readList(TRANSACTIONS_FILE, Transaction.class));

        Transaction transaction = Transaction.successful(payerId, payeeId, amount, reference);
        transaction.setId(IdGenerator.nextId(transactions));
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);

        transactions.add(transaction);
        fileManager.writeList(TRANSACTIONS_FILE, transactions);
        return transaction;
    }

    public List<Transaction> findByUser(Long userId) {
        List<Transaction> all = new ArrayList<> (fileManager.readList(TRANSACTIONS_FILE, Transaction.class));
        List<Transaction> result = new ArrayList<> ();

        for(Transaction tx : all){
                if(tx.getPayerId().equals(userId) || tx.getPayeeId().equals(userId)){
                        result.add(tx);
                }
        }
        return result;
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(fileManager.readList(TRANSACTIONS_FILE, Transaction.class));
    }
}

