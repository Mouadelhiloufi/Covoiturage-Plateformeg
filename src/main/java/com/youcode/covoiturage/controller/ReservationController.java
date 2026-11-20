package com.youcode.covoiturage.controller;

import com.youcode.covoiturage.dto.ReservationRequest;
import com.youcode.covoiturage.dto.ReservationResponse;
import com.youcode.covoiturage.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> reserver(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.creerReservation(request));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<ReservationResponse> annuler(@PathVariable Long id, @RequestParam Long passagerId) {
        return ResponseEntity.ok(reservationService.annulerReservation(id, passagerId));
    }

    @GetMapping("/passager/{passagerId}")
    public ResponseEntity<List<ReservationResponse>> getByPassager(@PathVariable Long passagerId) {
        return ResponseEntity.ok(reservationService.getReservationsByPassager(passagerId));
    }
}