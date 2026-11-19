package com.youcode.covoiturage.mapper;

import com.youcode.covoiturage.dto.ConducteurRequest;
import com.youcode.covoiturage.dto.ConducteurResponse;
import com.youcode.covoiturage.entity.Conducteur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConducteurMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicules", ignore = true)
    Conducteur toEntity(ConducteurRequest request);

    ConducteurResponse toResponse(Conducteur conducteur);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicules", ignore = true)
    void updateEntityFromRequest(ConducteurRequest request, @MappingTarget Conducteur conducteur);
}