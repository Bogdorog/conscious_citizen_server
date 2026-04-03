package com.sergeev.conscious_citizen_server.incident.api.dto;

import java.util.Date;

public record IncidentShortResponse(
        Long id,
        String title,
        String type,
        double latitude,
        double longitude,
        Date created
) {}
