package com.youcode.covoiturage.mapper;

import com.youcode.covoiturage.dto.TrajetResponse;
import com.youcode.covoiturage.entity.Trajet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PointArretMapper.class})
public interface TrajetMapper {

    @Mapping(target = "conducteurId", source = "conducteur.id")
    @Mapping(target = "conducteurNomComplet", expression = "java(trajet.getConducteur().getPrenom() + \" \" + trajet.getConducteur().getNom())")
    @Mapping(target = "vehiculeId", source = "vehicule.id")
    @Mapping(target = "vehiculeInfo", expression = "java(trajet.getVehicule().getMarque() + \" \" + trajet.getVehicule().getModele() + \" (\" + trajet.getVehicule().getImmatriculation() + \")\")")
    TrajetResponse toResponse(Trajet trajet);
}