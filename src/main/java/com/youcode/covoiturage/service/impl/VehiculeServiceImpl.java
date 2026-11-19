package com.youcode.covoiturage.service.impl;

import com.youcode.covoiturage.dto.VehiculeRequest;
import com.youcode.covoiturage.dto.VehiculeResponse;
import com.youcode.covoiturage.entity.Conducteur;
import com.youcode.covoiturage.entity.Vehicule;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.BusinessRuleException;
import com.youcode.covoiturage.exception.ForbiddenActionException;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.VehiculeMapper;
import com.youcode.covoiturage.repository.ConducteurRepository;
import com.youcode.covoiturage.repository.TrajetRepository;
import com.youcode.covoiturage.repository.VehiculeRepository;
import com.youcode.covoiturage.service.VehiculeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final ConducteurRepository conducteurRepository;
    private final TrajetRepository trajetRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public VehiculeResponse ajouterVehicule(VehiculeRequest request) {
        validerCapacite(request.capacite());

        if (vehiculeRepository.existsByImmatriculation(request.immatriculation())) {
            throw new BusinessRuleException("Un véhicule avec l'immatriculation '" + request.immatriculation() + "' existe déjà.");
        }

        Conducteur conducteur = conducteurRepository.findById(request.conducteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur introuvable avec l'ID : " + request.conducteurId()));

        Vehicule vehicule = vehiculeMapper.toEntity(request);
        vehicule.setConducteur(conducteur);

        Vehicule saved = vehiculeRepository.save(vehicule);
        log.info("Véhicule {} ajouté pour le conducteur ID {}", saved.getImmatriculation(), conducteur.getId());
        return vehiculeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponse getVehiculeById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable avec l'ID : " + id));
        return vehiculeMapper.toResponse(vehicule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> getVehiculesByConducteur(Long conducteurId) {
        if (!conducteurRepository.existsById(conducteurId)) {
            throw new ResourceNotFoundException("Conducteur introuvable avec l'ID : " + conducteurId);
        }
        return vehiculeRepository.findByConducteurId(conducteurId).stream()
                .map(vehiculeMapper::toResponse)
                .toList();
    }

    @Override
    public VehiculeResponse updateVehicule(Long id, VehiculeRequest request) {
        validerCapacite(request.capacite());

        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable avec l'ID : " + id));

        if (!vehicule.getConducteur().getId().equals(request.conducteurId())) {
            throw new ForbiddenActionException("Vous ne pouvez pas modifier le véhicule d'un autre conducteur.");
        }

        if (vehiculeRepository.existsByImmatriculationAndIdNot(request.immatriculation(), id)) {
            throw new BusinessRuleException("L'immatriculation '" + request.immatriculation() + "' est déjà utilisée.");
        }

        vehiculeMapper.updateEntityFromRequest(request, vehicule);
        Vehicule updated = vehiculeRepository.save(vehicule);
        log.info("Véhicule ID {} mis à jour", id);
        return vehiculeMapper.toResponse(updated);
    }

    @Override
    public void supprimerVehicule(Long id, Long conducteurId) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable avec l'ID : " + id));

        if (!vehicule.getConducteur().getId().equals(conducteurId)) {
            throw new ForbiddenActionException("Vous ne pouvez pas supprimer le véhicule d'un autre conducteur.");
        }

        if (trajetRepository.existsByVehiculeId(id)) {
            throw new BusinessRuleException("Impossible de supprimer ce véhicule car il est déjà associé à un ou plusieurs trajets.");
        }

        vehiculeRepository.delete(vehicule);
        log.info("Véhicule ID {} supprimé par le conducteur ID {}", id, conducteurId);
    }

    private void validerCapacite(Integer capacite) {
        if (capacite == null || capacite < 1 || capacite > 8) {
            throw new BadRequestException("La capacité du véhicule doit être comprise entre 1 et 8 places passagers.");
        }
    }
}