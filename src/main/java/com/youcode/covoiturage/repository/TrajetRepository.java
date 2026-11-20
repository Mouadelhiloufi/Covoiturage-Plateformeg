package com.youcode.covoiturage.repository;

import com.youcode.covoiturage.entity.Trajet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    boolean existsByVehiculeId(Long vehiculeId);

    @Query("""
        SELECT DISTINCT t FROM Trajet t
        LEFT JOIN FETCH t.conducteur
        LEFT JOIN FETCH t.vehicule
        LEFT JOIN FETCH t.pointsArret
        WHERE t.id = :id
    """)
    Optional<Trajet> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT t FROM Trajet t
        LEFT JOIN FETCH t.conducteur
        LEFT JOIN FETCH t.vehicule
        LEFT JOIN FETCH t.pointsArret
        WHERE t.conducteur.id = :conducteurId
        ORDER BY t.dateDepart DESC
    """)
    List<Trajet> findByConducteurIdWithDetails(@Param("conducteurId") Long conducteurId);

    @Query("""
    SELECT t FROM Trajet t
    JOIN t.pointsArret p1
    JOIN t.pointsArret p2
    WHERE t.statut = 'PUBLIE'
      AND LOWER(p1.ville) = LOWER(:villeDepart)
      AND LOWER(p2.ville) = LOWER(:villeArrivee)
      AND p1.ordre < p2.ordre
      AND CAST(t.dateDepart AS date) = :dateDepart
      AND (:prixMax IS NULL OR (p2.prixDepuisDepart - p1.prixDepuisDepart) <= :prixMax)
      AND (t.placesProposees - (
          SELECT COALESCE(SUM(r.nombrePlaces), 0)
          FROM Reservation r
          WHERE r.trajet = t AND r.statut = 'CONFIRMEE'
      )) >= :nbPlaces
    ORDER BY t.dateDepart ASC
""")
    Page<Trajet> rechercherTrajets(
            @Param("villeDepart") String villeDepart,
            @Param("villeArrivee") String villeArrivee,
            @Param("dateDepart") LocalDate dateDepart,
            @Param("prixMax") BigDecimal prixMax,
            @Param("nbPlaces") Integer nbPlaces,
            Pageable pageable
    );
}