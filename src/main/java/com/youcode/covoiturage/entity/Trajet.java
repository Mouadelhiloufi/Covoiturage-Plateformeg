package com.youcode.covoiturage.entity;

import com.youcode.covoiturage.enums.StatutReservation;
import com.youcode.covoiturage.enums.StatutTrajet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @Column(nullable = false)
    private LocalDateTime dateDepart;

    @Column(nullable = false)
    private Integer placesProposees;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTrajet statut;

    @OneToMany(mappedBy = "trajet", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    @Builder.Default
    private List<PointArret> pointsArret = new ArrayList<>();

    @OneToMany(mappedBy = "trajet")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();



    @Transient
    public String getVilleDepart() {
        return pointsArret.isEmpty() ? null : pointsArret.get(0).getVille();
    }

    @Transient
    public String getVilleArrivee() {
        return pointsArret.isEmpty() ? null : pointsArret.get(pointsArret.size() - 1).getVille();
    }

    @Transient
    public Integer getPlacesRestantes() {
        int placesReservees = reservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE)
                .mapToInt(Reservation::getNombrePlaces)
                .sum();
        return placesProposees - placesReservees;
    }

//    helpers

    public void addPointArret(PointArret pointArret) {
        pointsArret.add(pointArret);
        pointArret.setTrajet(this);
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setTrajet(this);
    }
}