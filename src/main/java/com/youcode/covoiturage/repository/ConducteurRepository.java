package com.youcode.covoiturage.repository;

import com.youcode.covoiturage.entity.Conducteur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConducteurRepository extends JpaRepository<Conducteur, Long> {

    boolean existsByEmail(String email);

    boolean existsByNumeroPermis(String numeroPermis);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByNumeroPermisAndIdNot(String numeroPermis, Long id);

    Optional<Conducteur> findByEmailIgnoreCase(String email);

    Page<Conducteur> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String email, Pageable pageable);
}