package com.sergeev.conscious_citizen_server.notification.api.dto.request;

public record SendEmailRequest(
        String to,
        String subject,
        String text
) {}
