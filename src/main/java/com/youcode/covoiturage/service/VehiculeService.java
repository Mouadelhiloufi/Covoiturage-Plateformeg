package com.youcode.covoiturage.service;

import com.youcode.covoiturage.dto.VehiculeRequest;
import com.youcode.covoiturage.dto.VehiculeResponse;

import java.util.List;

public interface VehiculeService {
    VehiculeResponse ajouterVehicule(VehiculeRequest request);
    VehiculeResponse getVehiculeById(Long id);
    List<VehiculeResponse> getVehiculesByConducteur(Long conducteurId);
    VehiculeResponse updateVehicule(Long id, VehiculeRequest request);
    void supprimerVehicule(Long id, Long conducteurId);
}