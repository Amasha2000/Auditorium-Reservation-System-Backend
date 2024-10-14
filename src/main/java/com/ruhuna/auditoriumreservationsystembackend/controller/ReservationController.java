package com.ruhuna.auditoriumreservationsystembackend.controller;

import com.ruhuna.auditoriumreservationsystembackend.dto.ReservationDto;
import com.ruhuna.auditoriumreservationsystembackend.dto.UnavailableDatesDto;
import com.ruhuna.auditoriumreservationsystembackend.dto.common.CommonResponse;
import com.ruhuna.reservationsystembackend.entity.GuestUser;
import com.ruhuna.reservationsystembackend.entity.Reservation;
import com.ruhuna.auditoriumreservationsystembackend.services.GuestUserService;
import com.ruhuna.auditoriumreservationsystembackend.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final GuestUserService guestUserService;
    private final ModelMapper modelMapper;

    //get reserved date details
    @GetMapping("/unavailable-dates")
    public List<UnavailableDatesDto> getUnavailableDates() {
        return reservationService.getUnavailableDates();
    }

    //get all reservations
    @GetMapping("/get_all")
    public List<Reservation> getAllReservations(){
        return reservationService.getAllReservations();
    }

    //get reservation by user Id
    @GetMapping("/get_user/{username}")
    public ResponseEntity<List<Reservation>> getReservationByUserName(@PathVariable String username) {
        // Fetch reservations by username
        List<Reservation> reservations = reservationService.getReservationsByUsername(username);
        return ResponseEntity.ok(reservations);
    }

    //form submission
    @PostMapping(value = "/submit-form",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitForm(@Valid @RequestBody ReservationDto reservationDto){
        try{
            reservationService.submitForm(reservationDto);
            return ResponseEntity.ok(new CommonResponse<>(true,"Form submitted successfully"));
        }catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    //update approval status
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> updateReservationStatus(@PathVariable Long id){
        try {
            reservationService.updateStatus(id);
            return ResponseEntity.ok(new CommonResponse<>(true, "Approval Status has changed successfully"));
        }catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @PutMapping("/cancel/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok().build();
    }


}
