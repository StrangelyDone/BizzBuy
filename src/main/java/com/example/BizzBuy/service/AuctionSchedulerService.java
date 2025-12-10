package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Auction;
import com.example.BizzBuy.model.Auction.AuctionStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AuctionSchedulerService {
    private final AuctionService auctionService;

    public AuctionSchedulerService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * Scheduled task that runs every 30 seconds to check and start auctions
     * whose start time has arrived (in IST timezone).
     */
    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    public void checkAndStartAuctions() {
        List<Auction> auctionsToStart = auctionService.findAuctionsToStart();

        for (Auction auction : auctionsToStart) {
            try {
                auction.setStatus(AuctionStatus.LIVE);
                auctionService.updateAuctionStatus(auction.getId(), AuctionStatus.LIVE);
                ZonedDateTime istNow = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
                System.out.println("Started auction ID: " + auction.getId() + " at " + istNow);
            } catch (Exception e) {
                System.err.println("Error starting auction " + auction.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Scheduled task that runs every 30 seconds to check and end auctions
     * whose end time has arrived (in IST timezone).
     */
    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    public void checkAndEndAuctions() {
        List<Auction> auctionsToEnd = auctionService.findAuctionsToEnd();

        for (Auction auction : auctionsToEnd) {
            try {
                // Determine winner before ending
                auctionService.determineWinner(auction.getId());

                // Update status to ENDED
                auctionService.updateAuctionStatus(auction.getId(), AuctionStatus.ENDED);

                ZonedDateTime istNow = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
                System.out.println("Ended auction ID: " + auction.getId() +
                        " at " + istNow +
                        " Winner ID: " + auction.getWinnerId());
            } catch (Exception e) {
                System.err.println("Error ending auction " + auction.getId() + ": " + e.getMessage());
            }
        }
    }
}
