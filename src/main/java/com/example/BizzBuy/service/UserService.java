package com.example.BizzBuy.service;

import com.example.BizzBuy.model.User;
// Role enum now inlined in User class
import com.example.BizzBuy.util.IdGenerator;
import com.example.BizzBuy.util.JsonFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USERS_FILE = "users.json";

    private final JsonFileManager fileManager;

    public User register(User newUser) {
        List<User> users = new ArrayList<>(fileManager.readList(USERS_FILE, User.class));
        Optional<User> existing = users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(newUser.getUsername()))
                .findFirst();
        User user;
        if (existing.isPresent()) {
            user = existing.get();
            user.setPassword(newUser.getPassword());
            user.setEmail(newUser.getEmail());
            user.setFullName(newUser.getFullName());
            // Update roles if provided, otherwise keep existing
            if (newUser.getRoles() != null && !newUser.getRoles().isEmpty()) {
                user.setRoles(newUser.getRoles());
            }
        } else {
            // Initialize roles for new user
            Set<User.Role> defaultRoles = new HashSet<>();
            if (newUser.getRoles() != null && !newUser.getRoles().isEmpty()) {
                defaultRoles.addAll(newUser.getRoles());
            } else {
                // By default, all users can act as both BUYER and SELLER
                defaultRoles.add(User.Role.BUYER);
                defaultRoles.add(User.Role.SELLER);
            }
            
            user = User.builder()
                    .id(IdGenerator.nextId(users))
                    .username(newUser.getUsername())
                    .password(newUser.getPassword())
                    .email(newUser.getEmail())
                    .fullName(newUser.getFullName())
                    .roles(defaultRoles)
                    .enabled(true)
                    .build();
            users.add(user);
        }
        fileManager.writeList(USERS_FILE, users);
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(fileManager.readList(USERS_FILE, User.class));
    }

    public User updateProfile(Long userId, String email, String fullName) {
        List<User> users = findAll();
        User user = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEmail(email);
        user.setFullName(fullName);
        fileManager.writeList(USERS_FILE, users);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public User findById(Long id) {
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User requireByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}

