package com.example.BizzBuy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private Long id;
    private Long userId;
    private String type; // PURCHASE, SALE, BID, OUTBID, BID_WON
    private String message;
    private boolean read;
    private LocalDateTime timestamp;
    private Long relatedId; // Product/Auction ID for reference
}
