package com.example.BizzBuy.service;

import com.example.BizzBuy.model.CartItem;
import com.example.BizzBuy.model.Order;
import com.example.BizzBuy.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SequenceGeneratorService sequenceGenerator;

    public Order createOrder(Long buyerId, List<CartItem> items, double total) {
        Order order = Order.builder()
                .id(sequenceGenerator.generateSequence("orders_sequence"))
                .buyerId(buyerId)
                .items(items)
                .totalAmount(total)
                .date(LocalDateTime.now())
                .status(Order.OrderStatus.PAID)
                .build();
        return orderRepository.save(order);
    }

    public List<Order> getOrders(Long userId) {
        return orderRepository.findByBuyerId(userId);
    }
}
