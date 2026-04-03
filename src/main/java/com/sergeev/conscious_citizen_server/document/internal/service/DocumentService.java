package com.sergeev.conscious_citizen_server.document.internal.service;

import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final IncidentRepository incidentRepository;
    private final DocumentTemplateService documentTemplateService;
    private final PdfGeneratorService pdfGenerator;
    private final FileStorageService storageService;

    public String generateDocument(Long incidentId) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow();

        // 1. грузим шаблон
        String template = documentTemplateService.loadTemplate("incident");

        // 2. подставляем данные
        Map<String, String> vars = Map.of(
                "title", incident.getTitle(),
                "description", incident.getMessage(),
                "address", incident.getAddress()
        );

        String html = documentTemplateService.fillTemplate(template, vars);

        // 3. генерим PDF
        byte[] pdf = pdfGenerator.generate(html);

        // 4. сохраняем файл
        String path = storageService.saveDocument(pdf, "incident_" + incidentId + ".pdf");

        // 5. сохраняем путь в БД
        incident.setFilePath(path);
        incidentRepository.save(incident);

        return path;
    }

    public byte[] download(Long incidentId) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow();

        if (incident.getFilePath() == null) {
            throw new IllegalStateException("Document not generated");
        }

        return storageService.read(incident.getFilePath());
    }
}
