package com.sergeev.conscious_citizen_server.incident.api.dto;

import java.time.LocalDateTime;

public record IncidentAdminResponse(
        Long id,
        String title,
        String type,
        String address,
        Long userId,
        LocalDateTime created
) {}
