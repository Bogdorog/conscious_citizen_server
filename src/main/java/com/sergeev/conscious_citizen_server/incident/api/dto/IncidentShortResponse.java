package com.sergeev.conscious_citizen_server.incident.api.dto;

public record IncidentShortResponse(
        Long id,
        String title,
        String type,
        double latitude,
        double longitude
) {}
