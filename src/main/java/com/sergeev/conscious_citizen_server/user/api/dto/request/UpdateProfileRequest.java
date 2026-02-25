package com.sergeev.conscious_citizen_server.user.api.dto.request;

public record UpdateProfileRequest(
        Long userId,
        String firstName,
        String lastName,
        String phone
) {}
