package com.sergeev.conscious_citizen_server.document.internal.service;

import com.sergeev.conscious_citizen_server.document.api.StorageService;
import com.sergeev.conscious_citizen_server.exception.DocumentNotFoundException;
import com.sergeev.conscious_citizen_server.exception.IncidentNotFoundException;
import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final IncidentRepository incidentRepository;
    private final DocumentTemplateService templateService;
    private final PdfGeneratorService pdfGenerator;
    private final StorageService storageService;
    private final IncidentDocumentVariablesExtractor variablesExtractor;

    @Async
    public void generateDocument(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));

        // Удаляем старый файл если есть
        if (incident.getFilePath() != null) {
            storageService.delete(incident.getFilePath());
        }

        String templateName = resolveTemplateName(incident);
        String template = templateService.loadTemplate(templateName);
        Map<String, String> vars = variablesExtractor.extract(incident);
        String html = templateService.fillTemplate(template, vars);

        byte[] pdf = pdfGenerator.generate(html, incident.getTitle());

        String storageKey = storageService.save(pdf, "incident_" + incidentId + ".pdf");

        incident.setFilePath(storageKey);
        incidentRepository.save(incident);

        log.info("Document generated for incident {}", incidentId);
    }

    @Async
    public void generateDocumentFromContent(Long incidentId, String html) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));

        // Удаляем старый файл если есть
        if (incident.getFilePath() != null) {
            storageService.delete(incident.getFilePath());
        }

        byte[] pdf = pdfGenerator.generate(html, incident.getTitle());

        String storageKey = storageService.save(pdf, "incident_" + incidentId + ".pdf");

        incident.setFilePath(storageKey);
        incidentRepository.save(incident);

        log.info("Document regenerated from custom content for incident {}", incidentId);
    }

    public byte[] download(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));

        if (incident.getFilePath() == null) {
            throw new DocumentNotFoundException("Document not generated for incident: " + incidentId);
        }

        return storageService.read(incident.getFilePath());
    }

    private String resolveTemplateName(Incident incident) {
        if (incident.getType() == null) {
            log.warn("Incident {} has no type, using default template", incident.getId());
            return "default";
        }
        return "incident_type_" + incident.getType().getName();
    }
}
