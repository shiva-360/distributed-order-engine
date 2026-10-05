package com.shivansh.auth_service.security;

import com.shivansh.auth_service.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    private final KeyProvider keys;
    private final String issuer;
    private final String audience;
    private final long accessMinutes;

    public JwtService(KeyProvider keys,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.audience}") String audience,
                      @Value("${app.jwt.access-token-minutes}") long accessMinutes) {
        this.keys = keys;
        this.issuer = issuer;
        this.audience = audience;
        this.accessMinutes = accessMinutes;
    }

    public String createAccessToken(User user) {
        Instant now = Instant.now();
        List<String> roles = List.of(user.getRoles().split(","));

        return Jwts.builder()
                .header().keyId(keys.getKeyId()).and()
                .issuer(issuer)
                .subject(user.getId().toString())
                .audience().add(audience).and()
                .id(UUID.randomUUID().toString())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(accessMinutes))))
                .signWith(keys.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public long accessTokenSeconds() {
        return accessMinutes * 60;
    }
}
