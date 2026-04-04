package com.sergeev.conscious_citizen_server.incident.api.dto;

import java.time.LocalDateTime;

public record IncidentShortResponse(
        Long id,
        String title,
        String type,
        double latitude,
        double longitude,
        Long userId,
        LocalDateTime created
) {}
