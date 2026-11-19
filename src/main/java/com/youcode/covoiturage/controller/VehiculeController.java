package com.youcode.covoiturage.controller;

import com.youcode.covoiturage.dto.VehiculeRequest;
import com.youcode.covoiturage.dto.VehiculeResponse;
import com.youcode.covoiturage.service.VehiculeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final VehiculeService vehiculeService;

    @PostMapping
    public ResponseEntity<VehiculeResponse> ajouter(@Valid @RequestBody VehiculeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeService.ajouterVehicule(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.getVehiculeById(id));
    }

    @GetMapping("/conducteur/{conducteurId}")
    public ResponseEntity<List<VehiculeResponse>> getByConducteur(@PathVariable Long conducteurId) {
        return ResponseEntity.ok(vehiculeService.getVehiculesByConducteur(conducteurId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeResponse> update(@PathVariable Long id, @Valid @RequestBody VehiculeRequest request) {
        return ResponseEntity.ok(vehiculeService.updateVehicule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id, @RequestParam Long conducteurId) {
        vehiculeService.supprimerVehicule(id, conducteurId);
        return ResponseEntity.noContent().build();
    }
}