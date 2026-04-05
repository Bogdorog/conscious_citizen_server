package com.sergeev.conscious_citizen_server.incident.api.dto;

import java.time.LocalDateTime;

public record IncidentResponse(
        Long id,
        String title,
        String type,
        String description,
        String address,
        String fullName,
        LocalDateTime created
) {}
