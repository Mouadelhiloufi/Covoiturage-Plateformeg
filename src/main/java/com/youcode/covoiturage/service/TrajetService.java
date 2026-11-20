package com.youcode.covoiturage.service;

import com.youcode.covoiturage.dto.TrajetRechercheResponse;
import com.youcode.covoiturage.dto.TrajetRequest;
import com.youcode.covoiturage.dto.TrajetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TrajetService {
    TrajetResponse publierTrajet(TrajetRequest request);
    TrajetResponse getTrajetDetails(Long id);
    List<TrajetResponse> getTrajetsByConducteur(Long conducteurId);
    Page<TrajetRechercheResponse> rechercherTrajets(
            String villeDepart,
            String villeArrivee,
            LocalDate dateDepart,
            BigDecimal prixMax,
            Integer nbPlaces,
            Pageable pageable
    );
    TrajetResponse terminerTrajet(Long trajetId, Long conducteurId);
    TrajetResponse annulerTrajet(Long trajetId, Long conducteurId);
}