package com.example.BizzBuy.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "auctions")
public class Auction {

    public enum AuctionStatus {
        SCHEDULED,
        LIVE,
        ENDED,
        CANCELLED
    }

    @Id
    private Long id;
    private Long itemId;
    private Long sellerId;
    private Double currentPrice;
    private Double startingPrice;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @Builder.Default
    private AuctionStatus status = AuctionStatus.SCHEDULED;
    private Long winnerId;
    private String winnerUsername;
}
