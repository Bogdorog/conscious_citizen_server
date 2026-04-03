package com.sergeev.conscious_citizen_server.incident.api.dto;

import java.util.Date;

public record IncidentResponse(
        Long id,
        String title,
        String type,
        String description,
        double latitude,
        double longitude,
        String address,
        Long userId,
        Date created
) {}
