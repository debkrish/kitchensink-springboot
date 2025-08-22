package com.example.kitchensink.controller;

import com.example.kitchensink.model.User;
import com.example.kitchensink.service.UserService;
import com.example.kitchensink.utils.JwtUtils;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    // In-memory users (username -> password)
    private static final Map<String, String> USER_STORE = Map.of(
            "alice", "password123",
            "bob", "securepass"
    );

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)  // e.g. "ROLE_ADMIN", "ROLE_USER"
                    .collect(Collectors.toList()));
            String token = jwtUtils.generateToken(authentication.getName(), claims);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        try{
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getRoles()
            );
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }

    }
    // Simple DTO for login request
    @Setter
    @Getter
    public static class LoginRequest {
        private String username;
        private String password;

    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private Set<String> roles; // pass ["USER"] or ["ADMIN","USER"]
    }
}
