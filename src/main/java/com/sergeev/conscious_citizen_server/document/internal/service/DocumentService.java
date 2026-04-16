package com.sergeev.conscious_citizen_server.document.internal.service;

import com.sergeev.conscious_citizen_server.document.api.StorageService;
import com.sergeev.conscious_citizen_server.document.api.event.DocumentGeneratedEvent;
import com.sergeev.conscious_citizen_server.exception.DocumentNotFoundException;
import com.sergeev.conscious_citizen_server.incident.api.IncidentApi;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final IncidentApi incidentApi;
    private final DocumentTemplateService templateService;
    private final PdfGeneratorService pdfGenerator;
    private final StorageService storageService;
    private final IncidentDocumentVariablesExtractor variablesExtractor;
    private final ApplicationEventPublisher publisher;

    @Async
    public void generateDocument(Long incidentId) {

        String filePath = incidentApi.getFilePathById(incidentId);

        // Удаляем старый файл если есть
        if (filePath != null) {
            storageService.delete(filePath);
        }
        /*
        Исправить в будущем, придумать как использовать только те переменные, что уже есть в инциденте
        String templateName = resolveTemplateName(incident);
        String template = templateService.loadTemplate(templateName);
        Map<String, String> vars = variablesExtractor.extract(incident);
        String html = templateService.fillTemplate(template, vars);

        byte[] pdf = pdfGenerator.generate(html, incident.getTitle());

        String storageKey = storageService.save(pdf, "incident_" + incidentId + ".pdf");

        publisher.publishEvent(
                new DocumentGeneratedEvent(
                        incidentId,
                        storageKey
                )
        );

         */
        log.debug("Создан документ для инцидента {}", incidentId);
    }

    @Async
    public void generateDocumentFromContent(Long incidentId, String html) {
        String filePath = incidentApi.getFilePathById(incidentId);

        // Удаляем старый файл если есть
        if (filePath != null) {
            storageService.delete(filePath);
        }

        byte[] pdf = pdfGenerator.generate(html, incidentId);

        String storageKey = storageService.save(pdf, "incident_" + incidentId + ".pdf");

        publisher.publishEvent(
                new DocumentGeneratedEvent(
                        incidentId,
                        storageKey
                )
        );

        log.debug("Создан документ для инцидента {}", incidentId);
    }

    public byte[] download(Long incidentId) {
        String filePath = incidentApi.getFilePathById(incidentId);

        if (filePath == null) {
            throw new DocumentNotFoundException("Документ не найден для инцидента: " + incidentId);
        }

        return storageService.read(filePath);
    }

    private String resolveTemplateName(IncidentResponse incident) {
        if (incident.type() == null) {
            log.warn("У инцидента {} отсутствует тип, использован стандартный шаблон", incident.id());
            return "default";
        }
        return "incident_type_" + incident.type();
    }
}
