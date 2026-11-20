package com.youcode.covoiturage.service.impl;

import com.youcode.covoiturage.dto.*;
import com.youcode.covoiturage.entity.*;
import com.youcode.covoiturage.enums.StatutReservation;
import com.youcode.covoiturage.enums.StatutTrajet;
import com.youcode.covoiturage.exception.*;
import com.youcode.covoiturage.mapper.*;
import com.youcode.covoiturage.repository.*;
import com.youcode.covoiturage.service.TrajetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrajetServiceImpl implements TrajetService {

    private final TrajetRepository trajetRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final TrajetMapper trajetMapper;
    private final PointArretMapper pointArretMapper;
    private final ReservationRepository reservationRepository;

    @Override
    public TrajetResponse publierTrajet(TrajetRequest request) {
        Conducteur conducteur = conducteurRepository.findById(request.conducteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur introuvable : " + request.conducteurId()));
        Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable : " + request.vehiculeId()));

        if (!vehicule.getConducteur().getId().equals(conducteur.getId())) {
            log.warn("Conducteur {} non propriétaire du véhicule {}", conducteur.getId(), vehicule.getId());
            throw new ForbiddenActionException("Ce véhicule n'appartient pas au conducteur spécifié.");
        }
        if (request.placesProposees() < 1 || request.placesProposees() > vehicule.getCapacite()) {
            throw new BadRequestException("Les places proposées doivent être entre 1 et " + vehicule.getCapacite());
        }

        List<PointArretRequest> points = validerEtTrierPoints(request.pointsArret());

        Trajet trajet = Trajet.builder()
                .conducteur(conducteur)
                .vehicule(vehicule)
                .dateDepart(points.get(0).heurePassage())
                .placesProposees(request.placesProposees())
                .statut(StatutTrajet.PUBLIE)
                .build();

        points.forEach(pt -> {
            PointArret point = pointArretMapper.toEntity(pt);
            point.setPrixDepuisDepart(pt.prixDepuisDepart().setScale(2, RoundingMode.HALF_UP));
            trajet.addPointArret(point);
        });

        Trajet saved = trajetRepository.save(trajet);
        log.info("Trajet ID {} publié ({} -> {})", saved.getId(), saved.getVilleDepart(), saved.getVilleArrivee());
        return trajetMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TrajetResponse getTrajetDetails(Long id) {
        return trajetRepository.findByIdWithDetails(id)
                .map(trajetMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrajetResponse> getTrajetsByConducteur(Long conducteurId) {
        if (!conducteurRepository.existsById(conducteurId)) {
            throw new ResourceNotFoundException("Conducteur introuvable : " + conducteurId);
        }
        return trajetRepository.findByConducteurIdWithDetails(conducteurId).stream()
                .map(trajetMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrajetRechercheResponse> rechercherTrajets(
            String villeDepart, String villeArrivee, LocalDate dateDepart,
            BigDecimal prixMax, Integer nbPlaces, Pageable pageable) {

        int placesMin = (nbPlaces != null && nbPlaces > 0) ? nbPlaces : 1;
        return trajetRepository
                .rechercherTrajets(villeDepart.trim(), villeArrivee.trim(), dateDepart, prixMax, placesMin, pageable)
                .map(t -> toRechercheResponse(t, villeDepart.trim(), villeArrivee.trim()));
    }

    private List<PointArretRequest> validerEtTrierPoints(List<PointArretRequest> points) {
        if (points == null || points.size() < 2) {
            throw new BadRequestException("Un trajet doit comporter au moins 2 points d'arrêt.");
        }

        List<PointArretRequest> tries = points.stream()
                .sorted(Comparator.comparingInt(PointArretRequest::ordre))
                .toList();

        if (!tries.get(0).heurePassage().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("L'heure de départ doit être dans le futur.");
        }
        if (tries.get(0).prixDepuisDepart().compareTo(BigDecimal.ZERO) != 0) {
            throw new BadRequestException("Le prix au point de départ doit être égal à 0.");
        }
        long villesUniques = tries.stream().map(p -> p.ville().trim().toLowerCase()).distinct().count();
        if (villesUniques != tries.size()) {
            throw new BadRequestException("Une ville ne peut apparaître qu'une seule fois sur le parcours.");
        }

        for (int i = 0; i < tries.size(); i++) {
            if (!tries.get(i).ordre().equals(i)) {
                throw new BadRequestException("Les ordres des points doivent être consécutifs (0, 1, 2...).");
            }
            if (i > 0) {
                PointArretRequest prev = tries.get(i - 1), curr = tries.get(i);
                if (!curr.heurePassage().isAfter(prev.heurePassage())
                        || curr.prixDepuisDepart().compareTo(prev.prixDepuisDepart()) <= 0) {
                    throw new BadRequestException("Les heures et les prix doivent être strictement croissants.");
                }
            }
        }
        return tries;
    }

    private TrajetRechercheResponse toRechercheResponse(Trajet t, String depart, String arrivee) {
        PointArret montee = t.getPointsArret().stream().filter(p -> p.getVille().equalsIgnoreCase(depart)).findFirst().orElseThrow();
        PointArret descente = t.getPointsArret().stream().filter(p -> p.getVille().equalsIgnoreCase(arrivee)).findFirst().orElseThrow();
        BigDecimal prixSegment = descente.getPrixDepuisDepart().subtract(montee.getPrixDepuisDepart()).setScale(2, RoundingMode.HALF_UP);

        return new TrajetRechercheResponse(
                t.getId(), t.getVilleDepart(), t.getVilleArrivee(),
                montee.getVille(), descente.getVille(),
                montee.getHeurePassage(), descente.getHeurePassage(),
                prixSegment, t.getPlacesRestantes(), t.getStatut(),
                t.getConducteur().getPrenom() + " " + t.getConducteur().getNom(),
                t.getVehicule().getMarque() + " " + t.getVehicule().getModele(),
                t.getPointsArret().stream().map(pointArretMapper::toResponse).toList()
        );
    }


    @Override
    public TrajetResponse terminerTrajet(Long trajetId, Long conducteurId) {
        Trajet trajet = trajetRepository.findByIdWithDetails(trajetId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable : " + trajetId));

        if (!trajet.getConducteur().getId().equals(conducteurId)) {
            log.warn("Conducteur {} non propriétaire du trajet {}", conducteurId, trajetId);
            throw new ForbiddenActionException("Vous ne pouvez pas modifier le trajet d'un autre conducteur.");
        }
        if (trajet.getStatut() != StatutTrajet.PUBLIE && trajet.getStatut() != StatutTrajet.COMPLET) {
            log.warn("Clôture refusée : statut invalide ({}) pour le trajet {}", trajet.getStatut(), trajetId);
            throw new BusinessRuleException("Seul un trajet PUBLIE ou COMPLET peut être terminé.");
        }
        if (!LocalDateTime.now().isAfter(trajet.getDateDepart())) {
            log.warn("Clôture refusée : heure de départ non dépassée pour le trajet {}", trajetId);
            throw new BusinessRuleException("Impossible de terminer un trajet avant son heure de départ.");
        }

        trajet.setStatut(StatutTrajet.TERMINE);
        Trajet saved = trajetRepository.save(trajet);
        log.info("Trajet ID {} terminé par le conducteur ID {}", trajetId, conducteurId);
        return trajetMapper.toResponse(saved);
    }

    @Override
    public TrajetResponse annulerTrajet(Long trajetId, Long conducteurId) {
        Trajet trajet = trajetRepository.findByIdWithDetails(trajetId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable : " + trajetId));

        if (!trajet.getConducteur().getId().equals(conducteurId)) {
            log.warn("Conducteur {} non propriétaire du trajet {}", conducteurId, trajetId);
            throw new ForbiddenActionException("Vous ne pouvez pas annuler le trajet d'un autre conducteur.");
        }
        if (trajet.getStatut() != StatutTrajet.PUBLIE && trajet.getStatut() != StatutTrajet.COMPLET) {
            log.warn("Annulation refusée : statut invalide ({}) pour le trajet {}", trajet.getStatut(), trajetId);
            throw new BusinessRuleException("Seul un trajet PUBLIE ou COMPLET peut être annulé.");
        }

        trajet.setStatut(StatutTrajet.ANNULE);

        List<Reservation> confirmees = reservationRepository.findByTrajetIdAndStatut(trajetId, StatutReservation.CONFIRMEE);
        confirmees.forEach(r -> r.setStatut(StatutReservation.ANNULEE));
        reservationRepository.saveAll(confirmees);

        Trajet saved = trajetRepository.save(trajet);
        log.info("Trajet ID {} annulé par le conducteur ID {} ({} réservation(s) annulée(s))",
                trajetId, conducteurId, confirmees.size());
        return trajetMapper.toResponse(saved);
    }


}