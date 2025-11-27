package com.example.BizzBuy.service;

import com.example.BizzBuy.model.CartItem;
import com.example.BizzBuy.model.Order;
// OrderStatus enum now inlined in Order class
import com.example.BizzBuy.util.IdGenerator;
import com.example.BizzBuy.util.JsonFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String ORDERS_FILE = "orders.json";
    private final JsonFileManager fileManager;

    public Order createOrder(Long buyerId, List<CartItem> items, double total) {
        List<Order> orders = new ArrayList<>(fileManager.readList(ORDERS_FILE, Order.class));
        Order order = Order.builder()
                .id(IdGenerator.nextId(orders))
                .buyerId(buyerId)
                .items(items)
                .totalAmount(total)
                .date(LocalDateTime.now())
                .status(Order.OrderStatus.PAID)
                .build();
        orders.add(order);
        fileManager.writeList(ORDERS_FILE, orders);
        return order;
    }

    public List<Order> getOrders(Long userId) {
        return new ArrayList<>(fileManager.readList(ORDERS_FILE, Order.class)).stream()
                .filter(order -> order.getBuyerId().equals(userId))
                .toList();
    }
}

