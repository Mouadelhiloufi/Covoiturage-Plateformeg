package com.youcode.covoiturage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TrajetRequest(
        @NotNull(message = "L'identifiant du conducteur est obligatoire")
        Long conducteurId,

        @NotNull(message = "L'identifiant du véhicule est obligatoire")
        Long vehiculeId,

        @NotNull(message = "Le nombre de places proposées est obligatoire")
        @Min(value = 1, message = "Il faut proposer au moins 1 place")
        Integer placesProposees,

        @NotEmpty(message = "La liste des points d'arrêt est obligatoire")
        @Valid
        List<PointArretRequest> pointsArret
) {}