package com.example.BizzBuy.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bids")
public class Bid {
    @Id
    private Long id;
    private Long auctionId;
    private Long bidderId;
    private Double amount;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
