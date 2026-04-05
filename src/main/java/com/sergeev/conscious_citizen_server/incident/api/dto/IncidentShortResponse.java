package com.sergeev.conscious_citizen_server.incident.api.dto;

import java.time.LocalDateTime;

public record IncidentShortResponse(
        Long id,
        String title,
        String type,
        Double latitude,
        Double longitude,
        Long userId,
        LocalDateTime created
) {}
