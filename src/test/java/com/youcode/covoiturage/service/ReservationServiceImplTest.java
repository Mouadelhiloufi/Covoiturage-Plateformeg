package com.youcode.covoiturage.service;

import com.youcode.covoiturage.dto.ReservationRequest;
import com.youcode.covoiturage.dto.ReservationResponse;
import com.youcode.covoiturage.entity.*;
import com.youcode.covoiturage.enums.StatutReservation;
import com.youcode.covoiturage.enums.StatutTrajet;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.BusinessRuleException;
import com.youcode.covoiturage.mapper.ReservationMapper;
import com.youcode.covoiturage.repository.*;
import com.youcode.covoiturage.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private TrajetRepository trajetRepository;
    @Mock private PassagerRepository passagerRepository;
    @Mock private PointArretRepository pointArretRepository;
    @Mock private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Passager salma;
    private Trajet trajet;
    private PointArret casa;
    private PointArret settat;
    private PointArret marrakech;

    @BeforeEach
    void setUp() {
        salma = Passager.builder().id(1L).nom("Alami").prenom("Salma").build();
        trajet = Trajet.builder()
                .id(10L)
                .placesProposees(3)
                .dateDepart(LocalDateTime.now().plusDays(3))
                .statut(StatutTrajet.PUBLIE)
                .build();

        casa = PointArret.builder().id(101L).ville("Casablanca").ordre(1).prixDepuisDepart(new BigDecimal("40.00")).trajet(trajet).build();
        settat = PointArret.builder().id(102L).ville("Settat").ordre(2).prixDepuisDepart(new BigDecimal("70.00")).trajet(trajet).build();
        marrakech = PointArret.builder().id(103L).ville("Marrakech").ordre(3).prixDepuisDepart(new BigDecimal("120.00")).trajet(trajet).build();
    }

    @Test
    void creerReservation_dernierePlace_calculeMontant() {
        when(passagerRepository.findById(1L)).thenReturn(Optional.of(salma));
        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));
        when(pointArretRepository.findById(102L)).thenReturn(Optional.of(settat));
        when(pointArretRepository.findById(103L)).thenReturn(Optional.of(marrakech));
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(1L, 10L, StatutReservation.CONFIRMEE)).thenReturn(false);
        when(reservationRepository.sumPlacesConfirmeesByTrajetId(10L)).thenReturn(2);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reservationMapper.toResponse(any(Reservation.class))).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            ReservationResponse resp = mock(ReservationResponse.class);
            when(resp.montantTotal()).thenReturn(r.getMontantTotal());
            return resp;
        });

        ReservationRequest req = new ReservationRequest(1L, 10L, 102L, 103L, 1);

        ReservationResponse response = reservationService.creerReservation(req);

        assertThat(response).isNotNull();
        assertThat(response.montantTotal()).isEqualByComparingTo("50.00"); // 120 - 70
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.COMPLET);
        verify(reservationRepository).save(any(Reservation.class));
    }


    @Test
    void creerReservation_mauvaisSens_leverBadRequest() {
        when(passagerRepository.findById(1L)).thenReturn(Optional.of(salma));
        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));
        when(pointArretRepository.findById(102L)).thenReturn(Optional.of(settat));
        when(pointArretRepository.findById(101L)).thenReturn(Optional.of(casa));

        ReservationRequest req = new ReservationRequest(1L, 10L, 102L, 101L, 1); // Settat (2) -> Casa (1)

        assertThatThrownBy(() -> reservationService.creerReservation(req))
                .isInstanceOf(BadRequestException.class);
    }


    @Test
    void annulerReservation_plusDe24h() {
        trajet.setStatut(StatutTrajet.COMPLET);
        Reservation reservation = Reservation.builder()
                .id(50L).passager(salma).trajet(trajet).statut(StatutReservation.CONFIRMEE).build();

        when(reservationRepository.findById(50L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        reservationService.annulerReservation(50L, 1L);

        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.ANNULEE);
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
    }


    @Test
    void annulerReservation_moinsDe24h_LeverBusinessRule() {
        trajet.setDateDepart(LocalDateTime.now().plusHours(10)); // moins de 24h
        Reservation reservation = Reservation.builder()
                .id(50L).passager(salma).trajet(trajet).statut(StatutReservation.CONFIRMEE).build();

        when(reservationRepository.findById(50L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.annulerReservation(50L, 1L))
                .isInstanceOf(BusinessRuleException.class);
    }





}