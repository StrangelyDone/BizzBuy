package com.example.BizzBuy.controller;

import com.example.BizzBuy.model.Bid;
import com.example.BizzBuy.model.User;
import com.example.BizzBuy.service.BidService;
import com.example.BizzBuy.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;
    private final UserService userService;

    // POST /api/bids/place - Place a bid on an auction
    @PostMapping("/place")
    public ResponseEntity<?> placeBid(@RequestHeader("X-USER") String username, @RequestBody BidRequest request) {
        try {
            User current = userService.requireByUsername(username);
            Bid bid = bidService.placeBid(request.getAuctionId(), current.getId(), request.getAmount());
            return ResponseEntity.ok(bid);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET /api/bids/auction/{id} - Get all bids for an auction
    @GetMapping("/auction/{id}")
    public ResponseEntity<?> getBidsForAuction(@PathVariable Long id) {
        try {
            List<Bid> bids = bidService.getBidsForAuction(id);
            return ResponseEntity.ok(bids);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET /api/bids/my-bids - Get current user's bids
    @GetMapping("/my-bids")
    public ResponseEntity<?> getMyBids(@RequestHeader("X-USER") String username) {
        try {
            User current = userService.requireByUsername(username);
            List<Bid> bids = bidService.getBidsByUser(current.getId());
            return ResponseEntity.ok(bids);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Inner class for bid request
    @lombok.Data
    public static class BidRequest {
        private Long auctionId;
        private Double amount;
    }
}
