package com.youcode.covoiturage.repository;

import com.youcode.covoiturage.entity.PointArret;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointArretRepository extends JpaRepository<PointArret, Long> {
}