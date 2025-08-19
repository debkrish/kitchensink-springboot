package com.example.kitchensink.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Data // generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password; // 🔑 should be stored as a BCrypt hash
    private Set<String> roles;     // e.g. "ROLE_USER", "ROLE_ADMIN"
}