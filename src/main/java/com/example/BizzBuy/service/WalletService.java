package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Wallet;
import com.example.BizzBuy.util.JsonFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private static final String WALLETS_FILE = "wallets.json";
    private final JsonFileManager fileManager;

    public Wallet initWallet(Long userId) {
        List<Wallet> wallets = new ArrayList<>(fileManager.readList(WALLETS_FILE, Wallet.class));
        return wallets.stream()
                .filter(wallet -> wallet.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> createWallet(userId, wallets));
    }

    private Wallet createWallet(Long userId, List<Wallet> wallets) {
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(0.0)
                .currency("USD")
                .build();
        wallets.add(wallet);
        fileManager.writeList(WALLETS_FILE, wallets);
        return wallet;
    }

    public Wallet getWallet(Long userId) {
        return new ArrayList<>(fileManager.readList(WALLETS_FILE, Wallet.class)).stream()
                .filter(wallet -> wallet.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> initWallet(userId));
    }

    public Wallet addFunds(Long userId, Double amount) {
        List<Wallet> wallets = new ArrayList<>(fileManager.readList(WALLETS_FILE, Wallet.class));
        Wallet wallet = wallets.stream()
                .filter(w -> w.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> createWallet(userId, wallets));
        wallet.setBalance(wallet.getBalance() + amount);
        fileManager.writeList(WALLETS_FILE, wallets);
        return wallet;
    }

    public void deduct(Long userId, double amount) {
        List<Wallet> wallets = new ArrayList<>(fileManager.readList(WALLETS_FILE, Wallet.class));
        Wallet wallet = wallets.stream()
                .filter(w -> w.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));
        if (wallet.getBalance() < amount) {
            throw new IllegalStateException("Insufficient funds");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        fileManager.writeList(WALLETS_FILE, wallets);
    }

    public void credit(Long userId, double amount) {
        List<Wallet> wallets = new ArrayList<>(fileManager.readList(WALLETS_FILE, Wallet.class));
        Wallet wallet = wallets.stream()
                .filter(w -> w.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> createWallet(userId, wallets));
        wallet.setBalance(wallet.getBalance() + amount);
        fileManager.writeList(WALLETS_FILE, wallets);
    }
}

