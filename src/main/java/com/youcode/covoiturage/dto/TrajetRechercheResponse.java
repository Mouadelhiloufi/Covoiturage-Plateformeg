package com.youcode.covoiturage.dto;

import com.youcode.covoiturage.enums.StatutTrajet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TrajetRechercheResponse(
        Long trajetId,
        String villeDepartTrajet,
        String villeArriveeTrajet,
        String segmentDepart,
        String segmentArrivee,
        LocalDateTime heureMontee,
        LocalDateTime heureDescente,
        BigDecimal prixSegmentParPlace,
        Integer placesRestantes,
        StatutTrajet statut,
        String conducteurNomComplet,
        String vehiculeInfo,
        List<PointArretResponse> pointsArret
) {}