package com.sergeev.conscious_citizen_server.media.api.dto;

import java.util.UUID;

public record StorageSaveResult(
        UUID id,
        String checksum,
        String filePath
) {}
