package com.example.BizzBuy.controller;

import com.example.BizzBuy.model.Auction;
import com.example.BizzBuy.model.User;
import com.example.BizzBuy.service.AuctionService;
import com.example.BizzBuy.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionService auctionService;
    private final UserService userService;

    // POST /api/auctions/create - Create new auction
    @PostMapping("/create")
    public ResponseEntity<?> createAuction(@RequestHeader("X-USER") String username, @RequestBody Auction auction) {
        try {
            User current = userService.requireByUsername(username);
            Auction created = auctionService.createAuction(auction, current.getId());
            return ResponseEntity.ok(created);
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

    // GET /api/auctions/active - Get all active auctions
    @GetMapping("/active")
    public ResponseEntity<?> getActiveAuctions() {
        try {
            List<Auction> auctions = auctionService.getActiveAuctions();
            return ResponseEntity.ok(auctions);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET /api/auctions/{id} - Get auction details
    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionById(@PathVariable Long id) {
        try {
            Auction auction = auctionService.getAuctionById(id);
            return ResponseEntity.ok(auction);
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET /api/auctions - Get all auctions (optional: filter by seller)
    @GetMapping
    public ResponseEntity<?> getAllAuctions(@RequestParam(required = false) Long sellerId) {
        try {
            List<Auction> auctions;
            if (sellerId != null) {
                auctions = auctionService.getAuctionsBySeller(sellerId);
            } else {
                auctions = auctionService.getAllAuctions();
            }
            return ResponseEntity.ok(auctions);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // POST /api/auctions/{id}/close - Close auction (Seller only)
    @PostMapping("/{id}/close")
    public ResponseEntity<?> closeAuction(@RequestHeader("X-USER") String username, @PathVariable Long id) {
        try {
            User current = userService.requireByUsername(username);
            Auction closed = auctionService.closeAuction(id, current.getId());
            return ResponseEntity.ok(closed);
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

    // DELETE /api/auctions/{id}/cancel - Cancel auction (Seller only, no bids)
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAuction(@RequestHeader("X-USER") String username, @PathVariable Long id) {
        try {
            User current = userService.requireByUsername(username);
            Auction cancelled = auctionService.cancelAuction(id, current.getId());
            return ResponseEntity.ok(cancelled);
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
}
