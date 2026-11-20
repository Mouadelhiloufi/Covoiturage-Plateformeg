package com.youcode.covoiturage.mapper;

import com.youcode.covoiturage.dto.PointArretRequest;
import com.youcode.covoiturage.dto.PointArretResponse;
import com.youcode.covoiturage.entity.PointArret;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointArretMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trajet", ignore = true)
    PointArret toEntity(PointArretRequest request);

    PointArretResponse toResponse(PointArret pointArret);
}