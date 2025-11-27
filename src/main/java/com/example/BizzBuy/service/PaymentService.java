package com.example.BizzBuy.service;

import com.example.BizzBuy.model.CartItem;
import com.example.BizzBuy.model.Order;
import com.example.BizzBuy.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductService productService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final OrderService orderService;

    public Order buyNow(Long buyerId, Long productId, int quantity) {
        Product product = productService.getProduct(productId);
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        double total = product.getPrice() * quantity;
        walletService.deduct(buyerId, total);
        walletService.credit(product.getSellerId(), total);
        productService.decreaseStock(productId, quantity);
        transactionService.log(buyerId, product.getSellerId(), total, "Direct purchase");
        CartItem item = CartItem.builder()
                .productId(productId)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .build();
        Order order = orderService.createOrder(buyerId, List.of(item), total);
        return order;
    }
}
