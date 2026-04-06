package com.sergeev.conscious_citizen_server.user.api.dto;

public record UsersForAdmin(
        Long id,
        String fullName,
        String email,
        String role,
        boolean active,
        Long count
) {}