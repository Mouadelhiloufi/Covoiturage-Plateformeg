package com.youcode.covoiturage.mapper;

import com.youcode.covoiturage.dto.ReservationResponse;
import com.youcode.covoiturage.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "trajetId", source = "trajet.id")
    @Mapping(target = "passagerId", source = "passager.id")
    @Mapping(target = "passagerNomComplet", expression = "java(reservation.getPassager().getPrenom() + \" \" + reservation.getPassager().getNom())")
    @Mapping(target = "villeMontee", source = "pointMontee.ville")
    @Mapping(target = "villeDescente", source = "pointDescente.ville")
    @Mapping(target = "heureMontee", source = "pointMontee.heurePassage")
    @Mapping(target = "heureDescente", source = "pointDescente.heurePassage")
    ReservationResponse toResponse(Reservation reservation);
}