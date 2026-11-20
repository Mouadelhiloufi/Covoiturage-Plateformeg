package com.youcode.covoiturage.service;

import com.youcode.covoiturage.dto.*;
import com.youcode.covoiturage.entity.*;
import com.youcode.covoiturage.enums.StatutReservation;
import com.youcode.covoiturage.enums.StatutTrajet;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.ForbiddenActionException;
import com.youcode.covoiturage.mapper.PointArretMapper;
import com.youcode.covoiturage.mapper.TrajetMapper;
import com.youcode.covoiturage.repository.*;
import com.youcode.covoiturage.service.impl.TrajetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrajetServiceImplTest {

    @Mock private TrajetRepository trajetRepository;
    @Mock private ConducteurRepository conducteurRepository;
    @Mock private VehiculeRepository vehiculeRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private TrajetMapper trajetMapper;
    @Mock private PointArretMapper pointArretMapper;

    @InjectMocks
    private TrajetServiceImpl trajetService;

    private Conducteur hamid;
    private Vehicule dacia;

    @BeforeEach
    void setUp() {
        hamid = Conducteur.builder().id(1L).nom("Bennani").prenom("Hamid").build();
        dacia = Vehicule.builder().id(1L).marque("Dacia").modele("Logan").capacite(4).conducteur(hamid).build();
    }

    @Test
    void publierTrajet_vehiculeAutreConducteur_doitLeverForbidden() {
        Conducteur autre = Conducteur.builder().id(2L).build();
        when(conducteurRepository.findById(2L)).thenReturn(Optional.of(autre));
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(dacia));

        TrajetRequest req = new TrajetRequest(2L, 1L, 3, List.of());

        assertThatThrownBy(() -> trajetService.publierTrajet(req))
                .isInstanceOf(ForbiddenActionException.class);
    }

    @Test
    void publierTrajet_depassementCapacite_doitLeverBadRequest() {
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(hamid));
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(dacia));

        TrajetRequest req = new TrajetRequest(1L, 1L, 5, List.of()); // capacité max = 4

        assertThatThrownBy(() -> trajetService.publierTrajet(req))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rechercherTrajets_calculPrixSegment_casNominal() {
        LocalDateTime depart = LocalDateTime.now().plusDays(2);
        PointArret p1 = PointArret.builder().ville("Rabat").ordre(0).heurePassage(depart).prixDepuisDepart(BigDecimal.ZERO).build();
        PointArret p2 = PointArret.builder().ville("Settat").ordre(1).heurePassage(depart.plusHours(2)).prixDepuisDepart(new BigDecimal("70.00")).build();
        PointArret p3 = PointArret.builder().ville("Marrakech").ordre(2).heurePassage(depart.plusHours(4)).prixDepuisDepart(new BigDecimal("120.00")).build();

        Trajet trajet = Trajet.builder()
                .id(10L).conducteur(hamid).vehicule(dacia)
                .dateDepart(depart).placesProposees(3).statut(StatutTrajet.PUBLIE)
                .pointsArret(List.of(p1, p2, p3))
                .build();

        when(trajetRepository.rechercherTrajets(eq("Settat"), eq("Marrakech"), any(), any(), eq(1), any()))
                .thenReturn(new PageImpl<>(List.of(trajet)));

        Page<TrajetRechercheResponse> result = trajetService.rechercherTrajets(
                "Settat", "Marrakech", LocalDate.now().plusDays(2), null, 1, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).prixSegmentParPlace()).isEqualByComparingTo("50.00");
    }

    @Test
    void annulerTrajet_doitAnnulerTrajetEtReservationsEnCascade() {
        Trajet trajet = Trajet.builder().id(10L).conducteur(hamid).statut(StatutTrajet.PUBLIE).build();
        Reservation res1 = Reservation.builder().id(100L).trajet(trajet).statut(StatutReservation.CONFIRMEE).build();
        Reservation res2 = Reservation.builder().id(101L).trajet(trajet).statut(StatutReservation.CONFIRMEE).build();

        when(trajetRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(trajet));
        when(reservationRepository.findByTrajetIdAndStatut(10L, StatutReservation.CONFIRMEE)).thenReturn(List.of(res1, res2));
        when(trajetRepository.save(trajet)).thenReturn(trajet);

        trajetService.annulerTrajet(10L, 1L);

        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.ANNULE);
        assertThat(res1.getStatut()).isEqualTo(StatutReservation.ANNULEE);
        assertThat(res2.getStatut()).isEqualTo(StatutReservation.ANNULEE);
        verify(reservationRepository).saveAll(List.of(res1, res2));
    }
}