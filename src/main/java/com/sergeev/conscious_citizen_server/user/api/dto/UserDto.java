package com.sergeev.conscious_citizen_server.user.api.dto;

public record UserDto(
        String fullName,
        String phone,
        String email,
        String address) {}
