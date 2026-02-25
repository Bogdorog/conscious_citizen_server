package com.sergeev.conscious_citizen_server.user.api.dto.request;

public record RegisterUserRequest(String fullName, String email, String phone, String password) {}