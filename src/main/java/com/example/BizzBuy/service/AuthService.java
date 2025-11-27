package com.example.BizzBuy.service;

import com.example.BizzBuy.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final WalletService walletService;

    public User register(User user) {
        User registeredUser = userService.register(user);
        walletService.initWallet(registeredUser.getId());
        return registeredUser;
    }

    public User login(User loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }
}

