package com.sergeev.conscious_citizen_server.incident.api.dto;

public record IncidentResponse(
        Long id,
        String title,
        String type,
        String description,
        double latitude,
        double longitude,
        String address,
        Long userId
) {}
