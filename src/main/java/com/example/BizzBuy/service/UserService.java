package com.example.BizzBuy.service;

import com.example.BizzBuy.model.User;
// Role enum now inlined in User class
import com.example.BizzBuy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SequenceGeneratorService sequenceGenerator;

    public User register(User newUser) {
        if (userRepository.existsByUsername(newUser.getUsername())) {
            User existing = userRepository.findByUsername(newUser.getUsername()).get();
            existing.setPassword(newUser.getPassword());
            existing.setEmail(newUser.getEmail());
            existing.setFullName(newUser.getFullName());
            // Update roles if provided, otherwise keep existing
            if (newUser.getRoles() != null && !newUser.getRoles().isEmpty()) {
                existing.setRoles(newUser.getRoles());
            }
            return userRepository.save(existing);
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

            User user = User.builder()
                    .id(sequenceGenerator.generateSequence("users_sequence"))
                    .username(newUser.getUsername())
                    .password(newUser.getPassword())
                    .email(newUser.getEmail())
                    .fullName(newUser.getFullName())
                    .roles(defaultRoles)
                    .enabled(true)
                    .build();
            return userRepository.save(user);
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User updateProfile(Long userId, String email, String fullName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEmail(email);
        user.setFullName(fullName);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User requireByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
