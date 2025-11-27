package com.example.BizzBuy.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Idea extends Product {
    private String patentStatus;
    private String pitchDeckUrl;
    private boolean ndaRequired;
}

