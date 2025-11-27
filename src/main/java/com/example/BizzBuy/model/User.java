package com.example.BizzBuy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public enum Role {
        BUYER,
        SELLER
    }

    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    private boolean enabled;

    // Helper methods for role management
    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public void removeRole(Role role) {
        if (roles != null) {
            roles.remove(role);
        }
    }
}
