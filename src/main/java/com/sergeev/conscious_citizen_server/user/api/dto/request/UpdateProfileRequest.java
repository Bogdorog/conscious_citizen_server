package com.sergeev.conscious_citizen_server.user.api.dto.request;

public record UpdateProfileRequest(
        String fullName,
        String phone,
        String email,
        String address
) {}
