package com.sergeev.conscious_citizen_server.notification.api.dto.request;

public record SendEmailWithAttachmentRequest(
        String to,
        String subject,
        String text,
        byte[] file,
        String filename
) {}
