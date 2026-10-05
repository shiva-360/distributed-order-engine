package com.shivansh.auth_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "refresh_token")
@Getter @Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token_hash", length = 64, nullable = false, unique = true)
    private String tokenHash;
    private Long userId;
    private UUID familyId;
    private boolean used;
    private boolean revoked;
    private Instant expiresAt;
    private Instant createdAt = Instant.now();
}
