package com.sergeev.conscious_citizen_server.user.api.dto;

public record UserProfileView(
        Long id,
        String email,
        String phone,
        String firstName,
        String lastName
) {}
