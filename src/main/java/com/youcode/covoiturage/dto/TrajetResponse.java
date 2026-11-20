package com.youcode.covoiturage.dto;

import com.youcode.covoiturage.enums.StatutTrajet;

import java.time.LocalDateTime;
import java.util.List;

public record TrajetResponse(
        Long id,
        String villeDepart,
        String villeArrivee,
        LocalDateTime dateDepart,
        Integer placesProposees,
        Integer placesRestantes,
        StatutTrajet statut,
        Long conducteurId,
        String conducteurNomComplet,
        Long vehiculeId,
        String vehiculeInfo,
        List<PointArretResponse> pointsArret
) {}