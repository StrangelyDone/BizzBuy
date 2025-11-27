package com.example.BizzBuy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    public enum OrderStatus {
        CREATED,
        PAID,
        SHIPPED,
        COMPLETED,
        CANCELLED
    }

    private Long id;
    private Long buyerId;
    private List<CartItem> items;
    private Double totalAmount;
    private LocalDateTime date;
    private OrderStatus status;
}
