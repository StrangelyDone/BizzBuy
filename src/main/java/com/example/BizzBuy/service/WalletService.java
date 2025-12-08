package com.example.BizzBuy.service;

import com.example.BizzBuy.model.Wallet;
import com.example.BizzBuy.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

        private final WalletRepository walletRepository;

        public Wallet initWallet(Long userId) {
                return walletRepository.findById(userId)
                                .orElseGet(() -> createWallet(userId));
        }

        private Wallet createWallet(Long userId) {
                Wallet wallet = new Wallet(userId, 0.0, "USD");
                return walletRepository.save(wallet);
        }

        public Wallet addFunds(Long userId, Double amount) {
                Wallet wallet = walletRepository.findById(userId)
                                .orElseGet(() -> createWallet(userId));

                wallet.setBalance(wallet.getBalance() + amount);
                return walletRepository.save(wallet);
        }

        public void deduct(Long userId, double amount) {
                Wallet wallet = walletRepository.findById(userId)
                                .orElseThrow(() -> new IllegalStateException("Wallet not found"));

                if (wallet.getBalance() < amount) {
                        throw new IllegalStateException("Insufficient funds");
                }

                wallet.setBalance(wallet.getBalance() - amount);
                walletRepository.save(wallet);
        }

        public void credit(Long userId, double amount) {
                Wallet wallet = walletRepository.findById(userId)
                                .orElseGet(() -> createWallet(userId));

                wallet.setBalance(wallet.getBalance() + amount);
                walletRepository.save(wallet);
        }
}
