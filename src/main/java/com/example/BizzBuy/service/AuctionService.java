package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Auction;
import com.example.BizzBuy.model.Auction.AuctionStatus;
import com.example.BizzBuy.model.Bid;
import com.example.BizzBuy.model.Product;
import com.example.BizzBuy.repository.AuctionRepository;
import com.example.BizzBuy.repository.BidRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ProductService productService;
    private final SequenceGeneratorService sequenceGenerator;
    private final WalletService walletService;
    private final UserService userService;

    public AuctionService(AuctionRepository auctionRepository, BidRepository bidRepository,
            ProductService productService, SequenceGeneratorService sequenceGenerator,
            WalletService walletService, UserService userService) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.productService = productService;
        this.sequenceGenerator = sequenceGenerator;
        this.walletService = walletService;
        this.userService = userService;
    }

    public Auction createAuction(Auction newAuction, Long sellerId) {
        // Validate product exists and belongs to seller
        Product product = productService.getProduct(newAuction.getItemId());
        if (!product.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("Only the product owner can create an auction");
        }

        // Validate end time is after start time
        if (newAuction.getEndTime().isBefore(newAuction.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check if auction already exists for this product
        if (auctionRepository.findByItemId(newAuction.getItemId()).isPresent()) {
            throw new IllegalArgumentException("An auction already exists for this product");
        }

        // Create auction
        Auction auction = Auction.builder()
                .id(sequenceGenerator.generateSequence("auctions_sequence"))
                .itemId(newAuction.getItemId())
                .sellerId(sellerId)
                .startingPrice(newAuction.getStartingPrice())
                .currentPrice(newAuction.getStartingPrice())
                .startTime(newAuction.getStartTime())
                .endTime(newAuction.getEndTime())
                .status(AuctionStatus.SCHEDULED)
                .winnerId(null)
                .build();

        // Mark product as auction item
        product.setIsAuction(true);
        productService.updateProduct(product);

        return auctionRepository.save(auction);
    }

    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    public Auction getAuctionById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
    }

    public List<Auction> getActiveAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.LIVE);
    }

    public List<Auction> getAuctionsBySeller(Long sellerId) {
        return auctionRepository.findBySellerId(sellerId);
    }

    public List<Auction> search(String keyword) {
        List<Auction> allAuctions = getAllAuctions();

        // If no keyword, return all auctions
        if (keyword == null || keyword.trim().isEmpty()) {
            return allAuctions;
        }

        String query = keyword.toLowerCase().trim();
        List<Auction> results = new ArrayList<>();

        for (Auction auction : allAuctions) {
            try {
                // Get the product associated with this auction
                Product product = productService.getProduct(auction.getItemId());

                // Search in product name, description, and tags
                boolean matches = false;

                if (product.getName() != null && product.getName().toLowerCase().contains(query)) {
                    matches = true;
                } else if (product.getDescription() != null && product.getDescription().toLowerCase().contains(query)) {
                    matches = true;
                } else if (product.getTags() != null) {
                    for (String tag : product.getTags()) {
                        if (tag != null && tag.toLowerCase().contains(query)) {
                            matches = true;
                            break;
                        }
                    }
                }

                if (matches) {
                    results.add(auction);
                }
            } catch (Exception e) {
                // Skip auctions with missing products
                continue;
            }
        }

        return results;
    }

    public Auction closeAuction(Long auctionId, Long sellerId) {
        Auction auction = getAuctionById(auctionId);

        if (!auction.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("Only the auction owner can close the auction");
        }

        if (auction.getStatus() == AuctionStatus.ENDED) {
            throw new IllegalStateException("Auction has already ended");
        }

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new IllegalStateException("Auction is already cancelled");
        }

        // Determine winner if there are bids
        determineWinner(auctionId);

        auction.setStatus(AuctionStatus.ENDED);
        return auctionRepository.save(auction);
    }

    public Auction cancelAuction(Long auctionId, Long sellerId) {
        Auction auction = getAuctionById(auctionId);

        if (!auction.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("Only the auction owner can cancel the auction");
        }

        if (auction.getStatus() == AuctionStatus.ENDED) {
            throw new IllegalStateException("Cannot cancel an ended auction");
        }

        // Check if there are any bids
        List<Bid> bids = bidRepository.findByAuctionIdOrderByAmountDesc(auctionId);
        if (!bids.isEmpty()) {
            throw new IllegalStateException("Cannot cancel auction with existing bids");
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        return auctionRepository.save(auction);
    }

    public void updateAuctionStatus(Long auctionId, AuctionStatus status) {
        Auction auction = getAuctionById(auctionId);
        auction.setStatus(status);
        auctionRepository.save(auction);
    }

    public void determineWinner(Long auctionId) {
        Auction auction = getAuctionById(auctionId);

        // Find highest bid
        bidRepository.findTopByAuctionIdOrderByAmountDesc(auctionId)
                .ifPresent(highestBid -> {
                    auction.setWinnerId(highestBid.getBidderId());
                    auction.setCurrentPrice(highestBid.getAmount());

                    // Set winner username
                    try {
                        String winnerUsername = userService.findById(highestBid.getBidderId()).getUsername();
                        auction.setWinnerUsername(winnerUsername);
                    } catch (Exception e) {
                        auction.setWinnerUsername("User #" + highestBid.getBidderId());
                    }

                    auctionRepository.save(auction);

                    // Process wallet transactions
                    try {
                        // Deduct money from winner's wallet
                        walletService.deduct(highestBid.getBidderId(), highestBid.getAmount());
                        System.out.println("Deducted " + highestBid.getAmount() + " from winner's wallet (User ID: "
                                + highestBid.getBidderId() + ")");

                        // Credit money to seller's wallet
                        walletService.credit(auction.getSellerId(), highestBid.getAmount());
                        System.out.println("Credited " + highestBid.getAmount() + " to seller's wallet (User ID: "
                                + auction.getSellerId() + ")");
                    } catch (Exception e) {
                        // Log error - this is critical so we should know about it
                        System.err.println("Failed to process wallet transaction for auction " + auctionId + ": "
                                + e.getMessage());
                        e.printStackTrace();
                    }

                    // Decrement product stock
                    try {
                        productService.decreaseStock(auction.getItemId(), 1);
                    } catch (Exception e) {
                        // Log error but don't fail the auction closure
                        System.err.println("Failed to decrease stock for auction " + auctionId + ": " + e.getMessage());
                    }
                });
    }

    // Methods for scheduler service
    public List<Auction> findAuctionsToStart() {
        // Get current time in IST timezone
        LocalDateTime istNow = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        return auctionRepository.findByStartTimeLessThanEqualAndStatus(
                istNow, AuctionStatus.SCHEDULED);
    }

    public List<Auction> findAuctionsToEnd() {
        // Get current time in IST timezone
        LocalDateTime istNow = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        return auctionRepository.findByEndTimeLessThanEqualAndStatus(
                istNow, AuctionStatus.LIVE);
    }
}
