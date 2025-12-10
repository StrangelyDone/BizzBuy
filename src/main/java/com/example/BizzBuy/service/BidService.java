package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Auction;
import com.example.BizzBuy.model.Auction.AuctionStatus;
import com.example.BizzBuy.model.Bid;
import com.example.BizzBuy.repository.AuctionRepository;
import com.example.BizzBuy.repository.BidRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BidService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final SequenceGeneratorService sequenceGenerator;
    private final NotificationService notificationService;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository,
            SequenceGeneratorService sequenceGenerator, NotificationService notificationService) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.sequenceGenerator = sequenceGenerator;
        this.notificationService = notificationService;
    }

    public Bid placeBid(Long auctionId, Long bidderId, Double bidAmount) {
        // Get auction
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // Validate auction is live
        if (auction.getStatus() != AuctionStatus.LIVE) {
            throw new IllegalStateException("Auction is not currently active. Status: " + auction.getStatus());
        }

        // Validate bidder is not the seller
        if (auction.getSellerId().equals(bidderId)) {
            throw new IllegalArgumentException("Seller cannot bid on their own auction");
        }

        // Validate bid amount is higher than current price
        if (bidAmount <= auction.getCurrentPrice()) {
            throw new IllegalArgumentException(
                    "Bid amount must be higher than current price: " + auction.getCurrentPrice());
        }

        // Create and save bid
        Bid bid = Bid.builder()
                .id(sequenceGenerator.generateSequence("bids_sequence"))
                .auctionId(auctionId)
                .bidderId(bidderId)
                .amount(bidAmount)
                .timestamp(LocalDateTime.now())
                .build();

        bidRepository.save(bid);

        // Update auction current price
        auction.setCurrentPrice(bidAmount);
        auctionRepository.save(auction);

        // Get all previous bidders for this auction
        List<Bid> previousBids = bidRepository.findByAuctionIdOrderByAmountDesc(auctionId);

        // Notify previous bidders that they've been outbid
        previousBids.stream()
                .map(Bid::getBidderId)
                .filter(id -> !id.equals(bidderId)) // Exclude current bidder
                .distinct()
                .forEach(previousBidderId -> {
                    notificationService.create(
                            previousBidderId,
                            "OUTBID",
                            String.format("You've been outbid on auction #%d! New bid: $%.2f", auctionId, bidAmount),
                            auctionId);
                });

        // Notify seller of new bid
        notificationService.create(
                auction.getSellerId(),
                "BID",
                String.format("New bid of $%.2f placed on your auction #%d", bidAmount, auctionId),
                auctionId);

        // Notify current bidder of successful bid
        notificationService.create(
                bidderId,
                "BID",
                String.format("Bid placed successfully! Amount: $%.2f on auction #%d", bidAmount, auctionId),
                auctionId);

        return bid;
    }

    public List<Bid> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuctionIdOrderByAmountDesc(auctionId);
    }

    public Bid getHighestBid(Long auctionId) {
        return bidRepository.findTopByAuctionIdOrderByAmountDesc(auctionId)
                .orElse(null);
    }

    public List<Bid> getBidsByUser(Long userId) {
        return bidRepository.findByBidderId(userId);
    }
}
