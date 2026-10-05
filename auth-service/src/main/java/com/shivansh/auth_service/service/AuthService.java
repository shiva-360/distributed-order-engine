package com.shivansh.auth_service.service;

import com.shivansh.auth_service.entity.User;
import com.shivansh.auth_service.repository.UserRepository;
import com.shivansh.auth_service.request.LoginRequest;
import com.shivansh.auth_service.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Transactional
    public Long register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(req.password()));
        user.setRoles("USER");
        return users.save(user).getId();
    }

    public User authenticate(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        User user = users.findByEmail(email)
                .orElseThrow(this::invalid);
        if (!user.isEnabled() || !encoder.matches(req.password(), user.getPasswordHash())) {
            throw invalid();
        }
        return user;
    }

    private ResponseStatusException invalid() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
}
