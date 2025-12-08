package com.example.BizzBuy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stores")
public class SellerStore {
    @Id
    private Long id;
    private Long ownerId;
    private String storeName;
    private String logoUrl;
    private Double rating;
}
