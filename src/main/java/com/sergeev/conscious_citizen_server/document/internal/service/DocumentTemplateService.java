package com.sergeev.conscious_citizen_server.document.internal.service;

import com.sergeev.conscious_citizen_server.exception.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DocumentTemplateService {

    // Находит все {{переменные}} в шаблоне
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    @Value("${app.storage.templates-path}")
    private String templatesPath;

    public String loadTemplate(String name) {
        try {
            Path path = Paths.get(templatesPath, name + ".html");
            return Files.readString(path);

        } catch (IOException e) {
            // Пробуем fallback-шаблон
            log.warn("Template '{}' not found, trying default", name);
            try {
                Path fallback = Paths.get(templatesPath, "default.html");
                return Files.readString(fallback);
            } catch (IOException ex) {
                throw new TemplateNotFoundException("Template not found: " + name, ex);
            }
        }
    }

    public String fillTemplate(String template, Map<String, String> vars) {
        logMissingVariables(template, vars);

        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);

        while (matcher.find()) {
            String key = matcher.group(1);
            // Если переменная есть — подставляем значение, иначе показываем [название]
            String replacement = vars.getOrDefault(key, "[" + key + "]");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private void logMissingVariables(String template, Map<String, String> vars) {
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        List<String> missing = new ArrayList<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            if (!vars.containsKey(key)) {
                missing.add(key);
            }
        }

        if (!missing.isEmpty()) {
            log.warn("Template variables not provided, will use placeholders: {}", missing);
        }
    }

    private void validateVariables(String template, Map<String, String> vars) {
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        List<String> missing = new ArrayList<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            if (!vars.containsKey(key)) {
                missing.add(key);
            }
        }

        if (!missing.isEmpty()) {
            log.warn("Template variables not provided: {}", missing);
            // Предупреждение, а не исключение — незаполненные переменные останутся в PDF
            // Поменяй на throw, если хочешь жёсткую валидацию
        }
    }
}