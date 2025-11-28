package com.example.BizzBuy.controller;

import com.example.BizzBuy.model.Order;
import com.example.BizzBuy.model.Transaction;
import com.example.BizzBuy.model.User;
import com.example.BizzBuy.model.Wallet;
import com.example.BizzBuy.service.OrderService;
import com.example.BizzBuy.service.TransactionService;
import com.example.BizzBuy.service.UserService;
import com.example.BizzBuy.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final OrderService orderService;

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("X-USER") String username) {
        try {
            User user = userService.requireByUsername(username);
            return ResponseEntity.ok(user);
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

    @PutMapping("/me")
    public ResponseEntity<?> update(@RequestHeader("X-USER") String username,
                                       @RequestBody User user) {
        try {
            User current = userService.requireByUsername(username);
            User updated = userService.updateProfile(current.getId(), user.getEmail(), user.getFullName());
            return ResponseEntity.ok(updated);
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

    @GetMapping("/me/wallet")
    public ResponseEntity<?> wallet(@RequestHeader("X-USER") String username) {
        try {
            User current = userService.requireByUsername(username);
            Wallet wallet = walletService.initWallet(current.getId());
            return ResponseEntity.ok(wallet);
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

    @PostMapping("/me/wallet/add")
    public ResponseEntity<?> topUp(@RequestHeader("X-USER") String username,
                                        @RequestParam Double amount) {
        try {
            User current = userService.requireByUsername(username);
            Wallet wallet = walletService.addFunds(current.getId(), amount);
            return ResponseEntity.ok(wallet);
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

    @GetMapping("/me/transactions")
    public ResponseEntity<?> transactions(@RequestHeader("X-USER") String username) {
        try {
            User current = userService.requireByUsername(username);
            List<Transaction> transactions = transactionService.findByUser(current.getId());
            return ResponseEntity.ok(transactions);
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

    @GetMapping("/me/orders")
    public ResponseEntity<?> orders(@RequestHeader("X-USER") String username) {
        try {
            User current = userService.requireByUsername(username);
            List<Order> orders = orderService.getOrders(current.getId());
            return ResponseEntity.ok(orders);
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


