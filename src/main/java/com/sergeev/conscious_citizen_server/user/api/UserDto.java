package com.sergeev.conscious_citizen_server.user.api;

public record UserDto(Long id, String fullName, String address, String email, String phone) {}
