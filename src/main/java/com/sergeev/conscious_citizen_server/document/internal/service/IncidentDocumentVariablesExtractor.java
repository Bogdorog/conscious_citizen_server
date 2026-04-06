package com.sergeev.conscious_citizen_server.document.internal.service;

import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class IncidentDocumentVariablesExtractor {

    public Map<String, String> extract(Incident incident) {
        Map<String, String> vars = new HashMap<>();

        vars.put("title",       nullToEmpty(incident.getTitle()));
        vars.put("description", nullToEmpty(incident.getMessage()));
        vars.put("address",     nullToEmpty(incident.getAddress()));
        vars.put("type",        incident.getType() != null ? incident.getType().getName() : "");
        vars.put("createdAt",   formatDate(incident.getCreatedAt()));

        // Добавляй новые поля здесь, не трогая сервисы
        return vars;
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
