package com.example.BizzBuy.repository;

import com.example.BizzBuy.model.Auction;
import com.example.BizzBuy.model.Auction.AuctionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends MongoRepository<Auction, Long> {
    List<Auction> findByStatus(AuctionStatus status);

    List<Auction> findBySellerId(Long sellerId);

    Optional<Auction> findByItemId(Long itemId);

    List<Auction> findByStartTimeLessThanEqualAndStatus(LocalDateTime time, AuctionStatus status);

    List<Auction> findByEndTimeLessThanEqualAndStatus(LocalDateTime time, AuctionStatus status);
}
