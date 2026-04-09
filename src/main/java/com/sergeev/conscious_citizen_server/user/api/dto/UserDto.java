package com.sergeev.conscious_citizen_server.user.api.dto;

public record UserDto(
        Long id,
        String username,
        String fullName,
        String phone,
        String email,
        String address,
        String role,
        String avatarUrl
) {}
