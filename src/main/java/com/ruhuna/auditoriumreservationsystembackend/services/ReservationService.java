package com.ruhuna.auditoriumreservationsystembackend.services;

import com.ruhuna.auditoriumreservationsystembackend.dto.ReservationDto;
import com.ruhuna.auditoriumreservationsystembackend.dto.UnavailableDatesDto;
import com.ruhuna.auditoriumreservationsystembackend.entity.Reservation;

import java.util.List;

public interface ReservationService {
    List<UnavailableDatesDto> getUnavailableDates();
    List<Reservation> getAllReservations();
    void submitForm(ReservationDto reservationDto);
    void updateStatus(Long reservationId);
    List<Reservation> getReservationsByUsername(String username);
    void cancelReservation(Long reservationId);
}
