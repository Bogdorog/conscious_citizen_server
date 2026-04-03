package com.sergeev.conscious_citizen_server.document.internal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${app.storage.documents-path}")
    private String documentsPath;

    public String saveDocument(byte[] content, String filename) {

        try {
            Path dir = Paths.get(documentsPath);
            Files.createDirectories(dir);

            String uniqueName = UUID.randomUUID() + "_" + filename;
            Path path = dir.resolve(uniqueName);

            Files.write(path, content);

            return path.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public byte[] read(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}
