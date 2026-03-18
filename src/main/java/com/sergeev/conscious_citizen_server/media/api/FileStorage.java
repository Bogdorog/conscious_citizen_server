package com.sergeev.conscious_citizen_server.media.api;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface FileStorage {
    CompletableFuture<UUID> save(MultipartFile file) throws Exception;
    CompletableFuture<InputStream> load(String filePath) throws Exception;
    CompletableFuture<Boolean> delete(String filePath) throws Exception;
}

