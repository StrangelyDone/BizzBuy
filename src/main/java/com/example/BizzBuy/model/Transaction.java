package com.example.BizzBuy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    public enum TransactionStatus {
        PENDING,
        SUCCESS,
        FAILED
    }

    private Long id;
    private String txnId;
    private Long payerId;
    private Long payeeId;
    private Double amount;
    private TransactionStatus status;
    private LocalDateTime timestamp;
    private String reference;

    public static Transaction successful(Long payerId, Long payeeId, Double amount, String reference) {
        return Transaction.builder()
                .txnId(UUID.randomUUID().toString())
                .payerId(payerId)
                .payeeId(payeeId)
                .amount(amount)
                .status(Transaction.TransactionStatus.SUCCESS)
                .timestamp(LocalDateTime.now())
                .reference(reference)
                .build();
    }
}
