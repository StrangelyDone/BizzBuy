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

        Wallet foundWallet = null;

        for(Wallet wallet : wallets){
                if(wallet.getUserId().equals(userId)){
                        foundWallet = wallet;
                        break;
                }
        }
        if(foundWallet != null){
                return foundWallet;
        }
        return createWallet(userId, wallets);
    }

    private Wallet createWallet(Long userId, List<Wallet> wallets) {
        Wallet wallet = new Wallet(userId, 0.0, "USD");
        wallets.add(wallet);
        fileManager.writeList(WALLETS_FILE, wallets);
        return wallet;
    }

    public Wallet addFunds(Long userId, Double amount) {
        List<Wallet> wallets = new ArrayList<>(fileManager.readList(WALLETS_FILE, Wallet.class));

        Wallet foundWallet = null;
        for(Wallet w : wallets){
                if(w.getUserId().equals(userId)){
                        foundWallet = w;
                        break;
                }
        }
        if(foundWallet == null){
                foundWallet = createWallet(userId,wallets);
        }
        foundWallet.setBalance(foundWallet.getBalance() + amount);
        fileManager.writeList(WALLETS_FILE,wallets);

        return foundWallet;

    }

    public void deduct(Long userId, double amount) {
        List<Wallet> wallets = new ArrayList<>(fileManager.readList(WALLETS_FILE, Wallet.class));

        Wallet foundWallet = null;

        for(Wallet w : wallets){
                if(w.getUserId().equals(userId)){
                        foundWallet = w;
                        break;
                }
        }

        if (foundWallet == null) {
                throw new IllegalStateException("Wallet not found");
        }

        if (foundWallet.getBalance() < amount) {
                throw new IllegalStateException("Insufficient funds");
        }

        foundWallet.setBalance(foundWallet.getBalance() - amount);
        fileManager.writeList(WALLETS_FILE,wallets);

    }

    public void credit(Long userId, double amount) {
        List<Wallet> wallets = new ArrayList<>(fileManager.readList(WALLETS_FILE, Wallet.class));

        Wallet foundWallet = null;

        for(Wallet w : wallets){
                if(w.getUserId().equals(userId)){
                        foundWallet = w;
                        break;
                }
        }

        if(foundWallet == null){
                foundWallet = createWallet(userId,wallets);
        }
        foundWallet.setBalance(foundWallet.getBalance() + amount);
        fileManager.writeList(WALLETS_FILE,wallets);
    }
}
