package com.sergeev.conscious_citizen_server.document.internal.service;

import com.sergeev.conscious_citizen_server.document.api.StorageService;
import com.sergeev.conscious_citizen_server.exception.DocumentStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStorageService implements StorageService {

    @Value("${app.storage.documents-path}")
    private String documentsPath;

    @Override
    public String save(byte[] content, String filename) {
        try {
            Path dir = Paths.get(documentsPath);
            Files.createDirectories(dir);

            // storageKey — просто уникальное имя файла, без абсолютного пути
            String storageKey = UUID.randomUUID() + "_" + filename;
            Path path = dir.resolve(storageKey);

            Files.write(path, content);
            log.info("Saved document: {}", storageKey);

            return storageKey;

        } catch (IOException e) {
            throw new DocumentStorageException("Failed to save file: " + filename, e);
        }
    }

    @Override
    public byte[] read(String storageKey) {
        try {
            Path path = Paths.get(documentsPath).resolve(storageKey);
            return Files.readAllBytes(path);

        } catch (IOException e) {
            throw new DocumentStorageException("Failed to read file: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Path path = Paths.get(documentsPath).resolve(storageKey);
            Files.deleteIfExists(path);
            log.info("Deleted document: {}", storageKey);

        } catch (IOException e) {
            throw new DocumentStorageException("Failed to delete file: " + storageKey, e);
        }
    }
}
