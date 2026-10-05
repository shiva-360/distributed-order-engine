package com.shivansh.auth_service.controller;

import com.shivansh.auth_service.entity.User;
import com.shivansh.auth_service.request.*;
import com.shivansh.auth_service.security.JwtService;
import com.shivansh.auth_service.service.AuthService;
import com.shivansh.auth_service.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> register(@Valid @RequestBody RegisterRequest req) {
        return Map.of("userId", authService.register(req));
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        User user = authService.authenticate(req);
        String access = jwtService.createAccessToken(user);
        String refresh = refreshTokenService.createForLogin(user.getId());
        return new LoginResponse(access, refresh, "Bearer", jwtService.accessTokenSeconds());
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest req) {
        TokenPair pair = refreshTokenService.rotate(req.refreshToken());
        return new LoginResponse(pair.accessToken(), pair.refreshToken(),
                "Bearer", jwtService.accessTokenSeconds());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequest req) {
        refreshTokenService.logout(req.refreshToken());
    }
}
