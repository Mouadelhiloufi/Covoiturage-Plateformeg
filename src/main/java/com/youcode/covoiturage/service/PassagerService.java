package com.youcode.covoiturage.service;

import com.youcode.covoiturage.dto.PassagerRequest;
import com.youcode.covoiturage.dto.PassagerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PassagerService {
    PassagerResponse creerPassager(PassagerRequest request);
    PassagerResponse getPassagerById(Long id);
    Page<PassagerResponse> getAllPassagers(Pageable pageable);
    PassagerResponse updatePassager(Long id, PassagerRequest request);
    Page<PassagerResponse> rechercherPassagers(String query, Pageable pageable);
}