package com.example.BizzBuy.model;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private Long sellerId;
    private String name;
    private String description;
    private List<String> images;
    private Double price;
    private Integer stockQuantity;
    private List<String> tags;
}


