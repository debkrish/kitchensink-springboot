package com.example.kitchensink.controller;

import com.example.kitchensink.utils.JwtUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;

    // In-memory users (username -> password)
    private static final Map<String, String> USER_STORE = Map.of(
            "alice", "password123",
            "bob", "securepass"
    );

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        // Validate username/password
        String storedPassword = USER_STORE.get(request.getUsername());
        if (storedPassword == null || !storedPassword.equals(request.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(request.getUsername());

        return ResponseEntity.ok(Map.of("token", token));
    }

    // Simple DTO for login request
    @Setter
    @Getter
    public static class LoginRequest {
        private String username;
        private String password;

    }
}
