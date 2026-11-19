package com.youcode.covoiturage.repository;

import com.youcode.covoiturage.entity.Passager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassagerRepository extends JpaRepository<Passager, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Passager> findByEmailIgnoreCase(String email);

    Page<Passager> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String email, Pageable pageable);
}