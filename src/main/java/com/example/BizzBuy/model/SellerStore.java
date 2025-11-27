package com.example.BizzBuy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerStore {
    private Long id;
    private Long ownerId;
    private String storeName;
    private String logoUrl;
    private Double rating;
}
