package com.sergeev.conscious_citizen_server.document.api;

public interface StorageService {
    // Возвращает storageKey — не зависит от физического расположения
    String save(byte[] content, String filename);
    byte[] read(String storageKey);
    void delete(String storageKey);
}
