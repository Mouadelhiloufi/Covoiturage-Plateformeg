package com.youcode.covoiturage.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CollectionId;
import org.hibernate.engine.internal.Cascade;
import org.springframework.stereotype.Service;
import com.covoiturage.entity.Vehicule;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity

public class Conducteur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String numeroPermis;

    @Column(nullable = false ,unique = true)
    private String telephone;

    @OneToMany(mappedBy = "conducteur" ,orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List <Vehicule> vehicules= new ArrayList<>();

//    @OneToMany(mappedBy = "conducteur")
//    @Builder.Default
//    private List<Trajet> trajets =new ArrayList<>();




}
