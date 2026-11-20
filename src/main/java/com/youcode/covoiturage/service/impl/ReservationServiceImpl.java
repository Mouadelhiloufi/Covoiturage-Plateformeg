package com.youcode.covoiturage.service.impl;

import com.youcode.covoiturage.dto.ReservationRequest;
import com.youcode.covoiturage.dto.ReservationResponse;
import com.youcode.covoiturage.entity.Passager;
import com.youcode.covoiturage.entity.PointArret;
import com.youcode.covoiturage.entity.Reservation;
import com.youcode.covoiturage.entity.Trajet;
import com.youcode.covoiturage.enums.StatutReservation;
import com.youcode.covoiturage.enums.StatutTrajet;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.BusinessRuleException;
import com.youcode.covoiturage.exception.ForbiddenActionException;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.ReservationMapper;
import com.youcode.covoiturage.repository.PassagerRepository;
import com.youcode.covoiturage.repository.PointArretRepository;
import com.youcode.covoiturage.repository.ReservationRepository;
import com.youcode.covoiturage.repository.TrajetRepository;
import com.youcode.covoiturage.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TrajetRepository trajetRepository;
    private final PassagerRepository passagerRepository;
    private final PointArretRepository pointArretRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationResponse creerReservation(ReservationRequest request) {
        Passager passager = passagerRepository.findById(request.passagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passager introuvable : " + request.passagerId()));
        Trajet trajet = trajetRepository.findById(request.trajetId())
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable : " + request.trajetId()));
        PointArret montee = pointArretRepository.findById(request.pointMonteeId())
                .orElseThrow(() -> new ResourceNotFoundException("Point de montée introuvable : " + request.pointMonteeId()));
        PointArret descente = pointArretRepository.findById(request.pointDescenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Point de descente introuvable : " + request.pointDescenteId()));

        // 1. Trajet doit être PUBLIE (409 sinon)
        if (trajet.getStatut() != StatutTrajet.PUBLIE) {
            log.warn("Réservation refusée : trajet {} a le statut {}", trajet.getId(), trajet.getStatut());
            throw new BusinessRuleException("Ce trajet n'est pas ouvert à la réservation (statut : " + trajet.getStatut() + ").");
        }

        // 2. Pas de double réservation CONFIRMEE du même passager sur ce trajet (409)
        if (reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(passager.getId(), trajet.getId(), StatutReservation.CONFIRMEE)) {
            log.warn("Réservation refusée : passager {} a déjà une réservation confirmée sur le trajet {}", passager.getId(), trajet.getId());
            throw new BusinessRuleException("Vous avez déjà une réservation confirmée sur ce trajet.");
        }

        // 3. Les deux points appartiennent au trajet et ordreMontee < ordreDescente (400)
        if (!montee.getTrajet().getId().equals(trajet.getId()) || !descente.getTrajet().getId().equals(trajet.getId())) {
            log.warn("Réservation refusée : points d'arrêt n'appartiennent pas au trajet {}", trajet.getId());
            throw new BadRequestException("Les points de montée et de descente doivent appartenir au trajet sélectionné.");
        }
        if (montee.getOrdre() >= descente.getOrdre()) {
            log.warn("Réservation refusée : ordre montée ({}) >= ordre descente ({})", montee.getOrdre(), descente.getOrdre());
            throw new BadRequestException("Le point de montée doit précéder le point de descente.");
        }

        // 4. Stock dynamique de places restantes (409 si insuffisant)
        int placesReservees = reservationRepository.sumPlacesConfirmeesByTrajetId(trajet.getId());
        int placesRestantes = trajet.getPlacesProposees() - placesReservees;
        if (request.nombrePlaces() > placesRestantes) {
            log.warn("Réservation refusée : {} places demandées mais {} disponibles sur trajet {}", request.nombrePlaces(), placesRestantes, trajet.getId());
            throw new BusinessRuleException("Places insuffisantes (disponibles : " + placesRestantes + ").");
        }

        // 5. Calcul montant total : (prixDescente - prixMontee) * nombrePlaces
        BigDecimal prixSegment = descente.getPrixDepuisDepart().subtract(montee.getPrixDepuisDepart());
        BigDecimal montantTotal = prixSegment.multiply(BigDecimal.valueOf(request.nombrePlaces())).setScale(2, RoundingMode.HALF_UP);

        Reservation reservation = Reservation.builder()
                .passager(passager)
                .trajet(trajet)
                .pointMontee(montee)
                .pointDescente(descente)
                .nombrePlaces(request.nombrePlaces())
                .montantTotal(montantTotal)
                .dateReservation(LocalDateTime.now())
                .statut(StatutReservation.CONFIRMEE)
                .build();

        Reservation saved = reservationRepository.save(reservation);

        // 6. Bascule automatique à COMPLET si places restantes == 0
        if (placesRestantes - request.nombrePlaces() == 0) {
            trajet.setStatut(StatutTrajet.COMPLET);
            trajetRepository.save(trajet);
            log.info("Trajet ID {} est désormais COMPLET", trajet.getId());
        }

        log.info("Réservation ID {} créée pour passager ID {} sur trajet ID {} ({} places, {} DH)",
                saved.getId(), passager.getId(), trajet.getId(), saved.getNombrePlaces(), saved.getMontantTotal());
        return reservationMapper.toResponse(saved);
    }

    @Override
    public ReservationResponse annulerReservation(Long reservationId, Long passagerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable : " + reservationId));

        // 1. Vérifier appartenance (403)
        if (!reservation.getPassager().getId().equals(passagerId)) {
            log.warn("Annulation refusée : passager {} n'est pas propriétaire de la réservation {}", passagerId, reservationId);
            throw new ForbiddenActionException("Vous ne pouvez pas annuler la réservation d'un autre passager.");
        }

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BusinessRuleException("Cette réservation est déjà annulée.");
        }

        // 2. Vérifier délai > 24h avant le départ du trajet (409)
        Trajet trajet = reservation.getTrajet();
        if (!LocalDateTime.now().isBefore(trajet.getDateDepart().minusHours(24))) {
            log.warn("Annulation refusée : moins de 24h avant le départ du trajet {}", trajet.getId());
            throw new BusinessRuleException("L'annulation n'est plus possible à moins de 24 heures du départ.");
        }

        // 3. Passer à ANNULEE et repasser le trajet à PUBLIE s'il était COMPLET
        reservation.setStatut(StatutReservation.ANNULEE);
        if (trajet.getStatut() == StatutTrajet.COMPLET) {
            trajet.setStatut(StatutTrajet.PUBLIE);
            trajetRepository.save(trajet);
            log.info("Trajet ID {} repassé au statut PUBLIE suite à une annulation", trajet.getId());
        }

        Reservation saved = reservationRepository.save(reservation);
        log.info("Réservation ID {} annulée par le passager ID {}", reservationId, passagerId);
        return reservationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByPassager(Long passagerId) {
        if (!passagerRepository.existsById(passagerId)) {
            throw new ResourceNotFoundException("Passager introuvable : " + passagerId);
        }
        return reservationRepository.findByPassagerIdWithDetails(passagerId).stream()
                .map(reservationMapper::toResponse)
                .toList();
    }
}