package com.youcode.covoiturage.service;

import com.youcode.covoiturage.dto.ConducteurRequest;
import com.youcode.covoiturage.dto.ConducteurResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConducteurService {
    ConducteurResponse creerConducteur(ConducteurRequest request);
    ConducteurResponse getConducteurById(Long id);
    Page<ConducteurResponse> getAllConducteurs(Pageable pageable);
    ConducteurResponse updateConducteur(Long id, ConducteurRequest request);
    Page<ConducteurResponse> rechercherConducteurs(String query, Pageable pageable);
}