package com.example.BizzBuy.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private Long id;
    private Long sellerId;
    private String name;
    private String description;
    private List<String> images;
    private Double price;
    private Integer stockQuantity;
    @Builder.Default
    private Boolean isAuction = false;
    private List<String> tags;
}
