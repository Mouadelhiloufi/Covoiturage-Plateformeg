package com.youcode.covoiturage.service.impl;

import com.youcode.covoiturage.dto.PassagerRequest;
import com.youcode.covoiturage.dto.PassagerResponse;
import com.youcode.covoiturage.entity.Passager;
import com.youcode.covoiturage.exception.BusinessRuleException;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.PassagerMapper;
import com.youcode.covoiturage.repository.PassagerRepository;
import com.youcode.covoiturage.service.PassagerService;
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
public class PassagerServiceImpl implements PassagerService {

    private final PassagerRepository passagerRepository;
    private final PassagerMapper passagerMapper;

    @Override
    public PassagerResponse creerPassager(PassagerRequest request) {
        if (passagerRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Un passager avec l'email '" + request.email() + "' existe déjà.");
        }

        Passager saved = passagerRepository.save(passagerMapper.toEntity(request));
        log.info("Passager créé avec succès (ID : {})", saved.getId());
        return passagerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PassagerResponse getPassagerById(Long id) {
        Passager passager = passagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passager introuvable avec l'ID : " + id));
        return passagerMapper.toResponse(passager);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PassagerResponse> getAllPassagers(Pageable pageable) {
        return passagerRepository.findAll(pageable).map(passagerMapper::toResponse);
    }

    @Override
    public PassagerResponse updatePassager(Long id, PassagerRequest request) {
        Passager passager = passagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passager introuvable avec l'ID : " + id));

        if (passagerRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new BusinessRuleException("L'email '" + request.email() + "' est déjà utilisé par un autre passager.");
        }

        passagerMapper.updateEntityFromRequest(request, passager);
        Passager updated = passagerRepository.save(passager);
        log.info("Profil du passager ID {} mis à jour", id);
        return passagerMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PassagerResponse> rechercherPassagers(String query, Pageable pageable) {
        return passagerRepository
                .findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable)
                .map(passagerMapper::toResponse);
    }
}