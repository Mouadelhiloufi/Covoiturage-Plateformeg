package com.youcode.covoiturage.mapper;

import com.youcode.covoiturage.dto.PassagerRequest;
import com.youcode.covoiturage.dto.PassagerResponse;
import com.youcode.covoiturage.entity.Passager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassagerMapper {

    @Mapping(target = "id", ignore = true)
    Passager toEntity(PassagerRequest request);

    PassagerResponse toResponse(Passager passager);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(PassagerRequest request, @MappingTarget Passager passager);
}