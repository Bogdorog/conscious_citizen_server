package com.sergeev.conscious_citizen_server.user.api.event;

public record PasswordResetRequestedEvent(String email, String token) {}
