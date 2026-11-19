package com.youcode.covoiturage.service.impl;

import com.youcode.covoiturage.dto.ConducteurRequest;
import com.youcode.covoiturage.dto.ConducteurResponse;
import com.youcode.covoiturage.entity.Conducteur;
import com.youcode.covoiturage.exception.BusinessRuleException;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.ConducteurMapper;
import com.youcode.covoiturage.repository.ConducteurRepository;
import com.youcode.covoiturage.service.ConducteurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConducteurServiceImpl implements ConducteurService {

    private final ConducteurRepository conducteurRepository;
    private final ConducteurMapper conducteurMapper;

    @Override
    public ConducteurResponse creerConducteur(ConducteurRequest request) {
        if (conducteurRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Un conducteur avec l'email '" + request.email() + "' existe déjà.");
        }
        if (conducteurRepository.existsByNumeroPermis(request.numeroPermis())) {
            throw new BusinessRuleException("Un conducteur avec le numéro de permis '" + request.numeroPermis() + "' existe déjà.");
        }

        Conducteur saved = conducteurRepository.save(conducteurMapper.toEntity(request));
        log.info("Conducteur créé avec succès (ID : {})", saved.getId());
        return conducteurMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ConducteurResponse getConducteurById(Long id) {
        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur introuvable avec l'ID : " + id));
        return conducteurMapper.toResponse(conducteur);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConducteurResponse> getAllConducteurs(Pageable pageable) {
        return conducteurRepository.findAll(pageable).map(conducteurMapper::toResponse);
    }

    @Override
    public ConducteurResponse updateConducteur(Long id, ConducteurRequest request) {
        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur introuvable avec l'ID : " + id));

        if (conducteurRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new BusinessRuleException("L'email '" + request.email() + "' est déjà utilisé par un autre conducteur.");
        }
        if (conducteurRepository.existsByNumeroPermisAndIdNot(request.numeroPermis(), id)) {
            throw new BusinessRuleException("Le numéro de permis '" + request.numeroPermis() + "' est déjà utilisé.");
        }

        conducteurMapper.updateEntityFromRequest(request, conducteur);
        Conducteur updated = conducteurRepository.save(conducteur);
        log.info("Profil du conducteur ID {} mis à jour", id);
        return conducteurMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConducteurResponse> rechercherConducteurs(String query, Pageable pageable) {
        return conducteurRepository
                .findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable)
                .map(conducteurMapper::toResponse);
    }
}