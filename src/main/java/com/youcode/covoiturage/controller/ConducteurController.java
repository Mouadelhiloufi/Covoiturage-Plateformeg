package com.youcode.covoiturage.controller;

import com.youcode.covoiturage.dto.ConducteurRequest;
import com.youcode.covoiturage.dto.ConducteurResponse;
import com.youcode.covoiturage.service.ConducteurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conducteurs")
@RequiredArgsConstructor
public class ConducteurController {

    private final ConducteurService conducteurService;

    @PostMapping
    public ResponseEntity<ConducteurResponse> creer(@Valid @RequestBody ConducteurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conducteurService.creerConducteur(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConducteurResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(conducteurService.getConducteurById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ConducteurResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(conducteurService.getAllConducteurs(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConducteurResponse> update(@PathVariable Long id, @Valid @RequestBody ConducteurRequest request) {
        return ResponseEntity.ok(conducteurService.updateConducteur(id, request));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ConducteurResponse>> search(@RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(conducteurService.rechercherConducteurs(q, pageable));
    }
}