package com.sergeev.conscious_citizen_server.media.internal.repository;

import com.sergeev.conscious_citizen_server.media.internal.model.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
    Optional<MediaAsset> findById(UUID id);
}

