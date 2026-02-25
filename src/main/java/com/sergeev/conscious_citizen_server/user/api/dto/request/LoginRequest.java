package com.sergeev.conscious_citizen_server.user.api.dto.request;

public record LoginRequest(
        String emailOrPhone,
        String password
) {}
