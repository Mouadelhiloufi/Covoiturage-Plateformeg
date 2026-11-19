package com.youcode.covoiturage.dto;

public record VehiculeResponse(
        Long id,
        String marque,
        String modele,
        String immatriculation,
        String couleur,
        Integer capacite,
        Long conducteurId,
        String conducteurNomComplet
) {}