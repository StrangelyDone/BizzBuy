package com.example.BizzBuy.controller;

import com.example.BizzBuy.model.Cart;
import com.example.BizzBuy.model.CartItem;
import com.example.BizzBuy.model.Order;
import com.example.BizzBuy.model.User;
import com.example.BizzBuy.service.CartService;
import com.example.BizzBuy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping("/my-cart")
    public ResponseEntity<?> cart(@RequestHeader("X-USER") String username) {
        try {
            User current = userService.requireByUsername(username);
            Cart cart = cartService.getCart(current.getId());
            return ResponseEntity.ok(cart);
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

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestHeader("X-USER") String username,
                                    @RequestBody CartItem cartItem) {
        try {
            User current = userService.requireByUsername(username);
            Cart cart = cartService.addItem(current.getId(), cartItem);
            return ResponseEntity.ok(cart);
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

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestHeader("X-USER") String username,
                                       @RequestBody CartItem cartItem) {
        try {
            User current = userService.requireByUsername(username);
            Cart cart = cartService.updateItem(current.getId(), cartItem);
            return ResponseEntity.ok(cart);
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

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> remove(@RequestHeader("X-USER") String username,
                                       @PathVariable Long itemId) {
        try {
            User current = userService.requireByUsername(username);
            Cart cart = cartService.removeItem(current.getId(), itemId);
            return ResponseEntity.ok(cart);
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

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestHeader("X-USER") String username) {
        try {
            User current = userService.requireByUsername(username);
            Order order = cartService.checkout(current.getId());
            return ResponseEntity.ok(order);
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

