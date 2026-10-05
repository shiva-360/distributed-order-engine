package com.shivansh.auth_service.service;

import com.shivansh.auth_service.entity.RefreshToken;
import com.shivansh.auth_service.entity.User;
import com.shivansh.auth_service.repository.RefreshTokenRepository;
import com.shivansh.auth_service.repository.UserRepository;
import com.shivansh.auth_service.request.TokenPair;
import com.shivansh.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static  final SecureRandom RANDOM = new SecureRandom();
    private final RefreshTokenRepository repo;
    private final UserRepository users;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-token-days}")
    private long days;


    @Transactional
    public String createForLogin(Long userId) {
        return create(userId, UUID.randomUUID());   // new family on each login
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public TokenPair rotate(String rawToken) {
        RefreshToken old = repo.findByTokenHash(sha256(rawToken))
                .orElseThrow(this::unauthorized);

        if (old.isRevoked() || old.getExpiresAt().isBefore(Instant.now())) {
            throw unauthorized();
        }

        if (repo.markUsed(old.getId()) == 0) {
            // token was already used -> possible theft
            repo.revokeFamily(old.getFamilyId());
            throw unauthorized();
        }

        User user = users.findById(old.getUserId())
                .filter(User::isEnabled)
                .orElseThrow(this::unauthorized);

        String newRaw = create(user.getId(), old.getFamilyId());
        return new TokenPair(jwtService.createAccessToken(user), newRaw);
    }

    private String create(Long userId, UUID familyId) {
        byte[] bytes = new byte[32];                 // 256 bits
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken rt = new RefreshToken();
        rt.setTokenHash(sha256(raw));
        rt.setUserId(userId);
        rt.setFamilyId(familyId);
        rt.setExpiresAt(Instant.now().plus(Duration.ofDays(days)));
        repo.save(rt);
        return raw;                                  // only time the raw token exists
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }

    private static String sha256(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);      // 64 characters
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Transactional
    public void logout(String rawToken) {
        repo.findByTokenHash(sha256(rawToken))
                .ifPresent(t -> repo.revokeFamily(t.getFamilyId()));
    }


}
