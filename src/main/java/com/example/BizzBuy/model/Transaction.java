package com.example.BizzBuy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    public enum TransactionStatus {
        PENDING,
        SUCCESS,
        FAILED
    }

    @Id
    private Long id;
    private String txnId;
    private Long payerId;
    private Long payeeId;
    private Double amount;
    private TransactionStatus status;
    private LocalDateTime timestamp;
    private String reference;

    public static Transaction successful(Long payerId, Long payeeId, Double amount, String reference) {
        return new Transaction(
                null,
                UUID.randomUUID().toString(),
                payerId,
                payeeId,
                amount,
                TransactionStatus.SUCCESS,
                LocalDateTime.now(),
                reference);
    }
}
