package com.youcode.covoiturage.service;

import com.youcode.covoiturage.dto.ReservationRequest;
import com.youcode.covoiturage.dto.ReservationResponse;

import java.util.List;

public interface ReservationService {
    ReservationResponse creerReservation(ReservationRequest request);
    ReservationResponse annulerReservation(Long reservationId, Long passagerId);
    List<ReservationResponse> getReservationsByPassager(Long passagerId);
}