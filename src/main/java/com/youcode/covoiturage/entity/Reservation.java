package com.youcode.covoiturage.entity;

import com.youcode.covoiturage.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passager_id", nullable = false)
    private Passager passager;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_montee_id", nullable = false)
    private PointArret pointMontee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_descente_id", nullable = false)
    private PointArret pointDescente;

    @Column(nullable = false)
    private Integer nombrePlaces;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(nullable = false)
    private LocalDateTime dateReservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut;
}