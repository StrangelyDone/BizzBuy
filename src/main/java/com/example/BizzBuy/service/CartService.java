package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Cart;
import com.example.BizzBuy.model.CartItem;
import com.example.BizzBuy.model.Order;
import com.example.BizzBuy.model.Product;
import com.example.BizzBuy.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final OrderService orderService;
    private final SequenceGeneratorService sequenceGenerator;

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    public Cart addItem(Long userId, CartItem newItem) {
        Product product = productService.getProduct(newItem.getProductId());
        if (product.getStockQuantity() < newItem.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        Cart cart = getCart(userId);

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        CartItem existing = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst()
                .orElse(null);
        if (existing == null) {
            CartItem item = CartItem.builder()
                    .id(sequenceGenerator.generateSequence("cart_item_sequence"))
                    .productId(product.getId())
                    .quantity(newItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            cart.getItems().add(item);
        } else {
            if (existing.getQuantity() + newItem.getQuantity() > product.getStockQuantity()) {
                throw new IllegalArgumentException("Quantity exceeds available stock");
            }
            existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
        }
        recalculate(cart);
        return cartRepository.save(cart);
    }

    public Cart updateItem(Long userId, CartItem updatedItem) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getItems() == null) {
            throw new IllegalArgumentException("Cart has no items");
        }
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(updatedItem.getProductId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        item.setQuantity(updatedItem.getQuantity());
        recalculate(cart);
        return cartRepository.save(cart);
    }

    public Cart removeItem(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        recalculate(cart);
        return cartRepository.save(cart);
    }

    public Order checkout(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        cart.getItems().forEach(item -> {
            Product product = productService.getProduct(item.getProductId());
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for " + product.getName());
            }
        });
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
        walletService.deduct(userId, total);
        Map<Long, Double> sellerTotals = new HashMap<>();
        cart.getItems().forEach(item -> {
            Product product = productService.getProduct(item.getProductId());
            productService.decreaseStock(product.getId(), item.getQuantity());
            sellerTotals.merge(product.getSellerId(), item.getUnitPrice() * item.getQuantity(), Double::sum);
        });
        sellerTotals.forEach((sellerId, amount) -> {
            walletService.credit(sellerId, amount);
            transactionService.log(userId, sellerId, amount, "Retail checkout");
        });
        Order order = orderService.createOrder(userId, new ArrayList<>(cart.getItems()), total);
        cart.getItems().clear();
        cart.setTotalValue(0.0);
        cartRepository.save(cart);
        return order;
    }

    private Cart createCart(Long userId) {
        Cart cart = Cart.builder()
                .id(userId) // Using userId as cartId for 1:1 mapping
                .userId(userId)
                .items(new ArrayList<>())
                .totalValue(0.0)
                .build();
        return cartRepository.save(cart);
    }

    private void recalculate(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
        cart.setTotalValue(total);
    }
}
