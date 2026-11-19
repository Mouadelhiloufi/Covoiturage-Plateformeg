package com.youcode.covoiturage.controller;

import com.youcode.covoiturage.dto.PassagerRequest;
import com.youcode.covoiturage.dto.PassagerResponse;
import com.youcode.covoiturage.service.PassagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passagers")
@RequiredArgsConstructor
public class PassagerController {

    private final PassagerService passagerService;

    @PostMapping
    public ResponseEntity<PassagerResponse> creer(@Valid @RequestBody PassagerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passagerService.creerPassager(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassagerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(passagerService.getPassagerById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PassagerResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(passagerService.getAllPassagers(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassagerResponse> update(@PathVariable Long id, @Valid @RequestBody PassagerRequest request) {
        return ResponseEntity.ok(passagerService.updatePassager(id, request));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PassagerResponse>> search(@RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(passagerService.rechercherPassagers(q, pageable));
    }
}