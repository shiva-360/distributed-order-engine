package com.shivansh.auth_service.request;

public record LoginResponse(String accessToken, String refreshToken, String tokenType, long expiresIn) {}

