package com.ruhuna.auditoriumreservationsystembackend.repository;

import com.ruhuna.auditoriumreservationsystembackend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Query("SELECT r FROM Reservation r WHERE r.user.username = :username AND r.reservedDate >= :currentDate")
    List<Reservation> findUpcomingReservationsByUserName(@Param("username") String username, @Param("currentDate") LocalDate currentDate);

}
