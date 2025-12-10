package com.example.BizzBuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BizzBuyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BizzBuyApplication.class, args);
	}

}
