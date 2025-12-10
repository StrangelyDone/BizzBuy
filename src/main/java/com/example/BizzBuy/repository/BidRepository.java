package com.example.BizzBuy.repository;

import com.example.BizzBuy.model.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends MongoRepository<Bid, Long> {
    List<Bid> findByAuctionIdOrderByAmountDesc(Long auctionId);

    List<Bid> findByBidderId(Long bidderId);

    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(Long auctionId);
}
