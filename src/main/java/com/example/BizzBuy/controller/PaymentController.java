package com.example.BizzBuy.controller;
import com.example.BizzBuy.model.Order;
import com.example.BizzBuy.model.Transaction;
import com.example.BizzBuy.model.User;
import com.example.BizzBuy.service.PaymentService;
import com.example.BizzBuy.service.TransactionService;
import com.example.BizzBuy.service.UserService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;   
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PaymentController{
    private final PaymentService paymentService;
    private final TransactionService transactionService;
    private final UserService userService;
    @PostMapping("/purchase/{itemId}")
    public ResponseEntity<?> buy(@RequestHeader("X-USER")String username,
            @PathVariable Long itemId,
            @RequestParam(defaultValue="1")@Min(1)int quantity){
        try {
            User current =userService.requireByUsername(username);
            Order order =paymentService.buyNow(current.getId(),itemId,quantity);
            return ResponseEntity.ok(order);}
        catch (IllegalArgumentException | IllegalStateException ex){
            Map<String, String> error =new HashMap<>();
            error.put("error",ex.getMessage());
            return ResponseEntity.badRequest().body(error);}
        catch (Exception ex){
            Map<String, String> error =new HashMap<>();
            error.put("error",ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);}}
    @GetMapping("/my-history")
    public ResponseEntity<?> myHistory(@RequestHeader("X-USER") String username){
        try {
            User current = userService.requireByUsername(username);
            List<Transaction> transactions =transactionService.findByUser(current.getId());
            return ResponseEntity.ok(transactions);}
        catch (IllegalArgumentException | IllegalStateException ex){
            Map<String, String> error =new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);}
        catch (Exception ex){
            Map<String, String> error =new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);}}}