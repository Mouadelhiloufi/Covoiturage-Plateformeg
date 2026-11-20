package com.youcode.covoiturage.dto;

import com.youcode.covoiturage.enums.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Integer nombrePlaces,
        BigDecimal montantTotal,
        LocalDateTime dateReservation,
        StatutReservation statut,
        Long trajetId,
        Long passagerId,
        String passagerNomComplet,
        String villeMontee,
        String villeDescente,
        LocalDateTime heureMontee,
        LocalDateTime heureDescente
) {}