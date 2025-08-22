package com.example.kitchensink.service;

import com.example.kitchensink.model.User;
import com.example.kitchensink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String username, String rawPassword, Set<String> roles) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken");
        }

        // Ensure roles are never null
        Set<String> assignedRoles = new HashSet<>();
        if (roles == null || roles.isEmpty()) {
            assignedRoles.add("USER"); // default role
        } else {
            assignedRoles.addAll(roles);
            // If ADMIN is present, make sure USER is also present
            if (assignedRoles.contains("ADMIN")) {
                assignedRoles.add("USER");
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(assignedRoles);

        return userRepository.save(user);
    }
}
