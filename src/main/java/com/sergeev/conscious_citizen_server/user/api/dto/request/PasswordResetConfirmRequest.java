package com.sergeev.conscious_citizen_server.user.api.dto.request;

public record PasswordResetConfirmRequest(
        String token,
        String newPassword
) {}
