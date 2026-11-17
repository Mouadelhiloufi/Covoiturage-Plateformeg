package com.youcode.covoiturage.entity;

import com.youcode.covoiturage.entity.Conducteur;
import com.youcode.covoiturage.entity.Trajet;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "vehicules",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicule_immatriculation", columnNames = "immatriculation")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marque;

    @Column(nullable = false)
    private String modele;

    @Column(nullable = false, unique = true)
    private String immatriculation;

    @Column(nullable = false)
    private String couleur;

    @Column(nullable = false)
    private Integer capacite; // Places passagers (1 à 8)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;
//
//    @OneToMany(mappedBy = "vehicule")
//    @Builder.Default
//    private List<Trajet> trajets=new ArrayList<>();
}