package com.sergeev.conscious_citizen_server.incident.api.dto;

public record IncidentShortResponse(
        Long id,
        String title,
        double latitude,
        double longitude
) {}
