package com.shivansh.auth_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Entity @Table(name = "app_user")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String passwordHash;
    private String roles;       // "USER" or "USER,ADMIN"
    private boolean enabled = true;
    private Instant createdAt = Instant.now();
}
