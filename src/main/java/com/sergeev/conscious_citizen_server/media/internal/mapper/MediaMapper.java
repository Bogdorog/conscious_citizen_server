package com.sergeev.conscious_citizen_server.media.internal.mapper;

import com.sergeev.conscious_citizen_server.media.api.dto.MediaAssetDto;
import com.sergeev.conscious_citizen_server.media.internal.model.MediaAsset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MediaMapper {
    MediaAsset toEntity(MediaAssetDto dto);
    @Mapping(target = "downloadUrl", source = "downloadUrl")
    MediaAssetDto toResponse(MediaAsset entity, String downloadUrl);
    MediaAssetDto toResponse(MediaAsset entity);
}
