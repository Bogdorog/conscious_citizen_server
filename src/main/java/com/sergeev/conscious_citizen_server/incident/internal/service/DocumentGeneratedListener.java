package com.sergeev.conscious_citizen_server.incident.internal.service;

import com.sergeev.conscious_citizen_server.document.api.event.DocumentGeneratedEvent;
import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentGeneratedListener {
    private final IncidentRepository incidentRepository;
    @ApplicationModuleListener
    public void handle(DocumentGeneratedEvent event) {
        Incident incident = incidentRepository.findById(event.id())
                .orElseThrow(() -> new RuntimeException("Инцидент не найден"));

        incident.setFilePath(event.filePath());
        incidentRepository.save(incident);
    }
}