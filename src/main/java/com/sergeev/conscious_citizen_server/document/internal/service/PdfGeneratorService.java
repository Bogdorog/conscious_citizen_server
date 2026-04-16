package com.sergeev.conscious_citizen_server.document.internal.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sergeev.conscious_citizen_server.exception.PdfGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;

@Service
@Slf4j
public class PdfGeneratorService {

    @Value("${app.storage.templates-path}")
    private String templatesPath;

    public byte[] generate(String html, Long incidentId) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, Paths.get(templatesPath).toUri().toString());
            builder.toStream(os);
            builder.run();

            log.debug("Создан pdf: {}", incidentId);
            return os.toByteArray();

        } catch (Exception e) {
            throw new PdfGenerationException("Создание pdf файла провалилось: " + incidentId, e);
        }
    }
}
