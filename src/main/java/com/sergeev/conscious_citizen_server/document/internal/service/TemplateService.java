package com.sergeev.conscious_citizen_server.document.internal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class TemplateService {

    @Value("${app.storage.templates-path}")
    private String templatesPath;

    public String loadTemplate(String name) {

        try {
            Path path = Paths.get(templatesPath, name + ".html");
            return Files.readString(path);

        } catch (IOException e) {
            throw new RuntimeException("Template not found", e);
        }
    }

    public String fillTemplate(String template, Map<String, String> vars) {

        String result = template;

        for (var entry : vars.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return result;
    }
}
