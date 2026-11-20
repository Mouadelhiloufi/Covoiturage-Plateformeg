package com.youcode.covoiturage.controller;

import com.youcode.covoiturage.dto.TrajetRechercheResponse;
import com.youcode.covoiturage.dto.TrajetRequest;
import com.youcode.covoiturage.dto.TrajetResponse;
import com.youcode.covoiturage.service.TrajetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trajets")
@RequiredArgsConstructor
public class TrajetController {

    private final TrajetService trajetService;

    @PostMapping
    public ResponseEntity<TrajetResponse> publier(@Valid @RequestBody TrajetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trajetService.publierTrajet(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrajetResponse> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(trajetService.getTrajetDetails(id));
    }

    @GetMapping("/conducteur/{conducteurId}")
    public ResponseEntity<List<TrajetResponse>> getByConducteur(@PathVariable Long conducteurId) {
        return ResponseEntity.ok(trajetService.getTrajetsByConducteur(conducteurId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TrajetRechercheResponse>> rechercher(
            @RequestParam String villeDepart,
            @RequestParam String villeArrivee,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(defaultValue = "1") Integer nbPlaces,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                trajetService.rechercherTrajets(villeDepart, villeArrivee, date, prixMax, nbPlaces, pageable)
        );
    }

    @PatchMapping("/{id}/terminer")
    public ResponseEntity<TrajetResponse> terminer(@PathVariable Long id, @RequestParam Long conducteurId) {
        return ResponseEntity.ok(trajetService.terminerTrajet(id, conducteurId));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<TrajetResponse> annuler(@PathVariable Long id, @RequestParam Long conducteurId) {
        return ResponseEntity.ok(trajetService.annulerTrajet(id, conducteurId));
    }

}