package com.ruhuna.auditoriumreservationsystembackend.services.impl;

import com.ruhuna.auditoriumreservationsystembackend.dto.ReservationDto;
import com.ruhuna.auditoriumreservationsystembackend.dto.UnavailableDatesDto;
import com.ruhuna.auditoriumreservationsystembackend.entity.GuestUser;
import com.ruhuna.auditoriumreservationsystembackend.entity.Reservation;
import com.ruhuna.auditoriumreservationsystembackend.enums.ApprovalStatus;
import com.ruhuna.auditoriumreservationsystembackend.repository.GuestUserRepository;
import com.ruhuna.auditoriumreservationsystembackend.repository.ReservationRepository;
import com.ruhuna.auditoriumreservationsystembackend.services.EmailService;
import com.ruhuna.auditoriumreservationsystembackend.services.NotificationService;
import com.ruhuna.reservationsystembackend.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestUserRepository guestUserRepository;
    private final ModelMapper modelMapper;

    private final EmailService emailService;

    private final NotificationService notificationService;

    //get all reservations
    @Override
    public List<UnavailableDatesDto> getUnavailableDates() {
        try {
            List<Reservation> reservations = reservationRepository.findAll();
            return reservations.stream()
                    .map(reservation -> new UnavailableDatesDto(
                            reservation.getReservedDate(),
                            reservation.getOrganizationName(),
                            reservation.getEventType()))
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        try {
            List<Reservation> allReservations = reservationRepository.findAll();
            return allReservations;
        }catch (Exception e){
            throw e;
        }
    }

    //submit application form
    @Override
    public void submitForm(ReservationDto reservationDto) {
        try {
            Reservation reservation = modelMapper.map(reservationDto, Reservation.class);

            GuestUser user = guestUserRepository.findByUsername(reservationDto.getUsername());
            reservation.setUser(user);

            if (reservationDto.getOrganizationName() != null && !reservationDto.getOrganizationName().isEmpty())
                reservation.setOrganizationName(reservationDto.getOrganizationName());
            if (reservationDto.getApplicantName() != null && !reservationDto.getApplicantName().isEmpty())
                reservation.setApplicantName(reservationDto.getApplicantName());
            if (reservationDto.getNic() != null && !reservationDto.getNic().isEmpty())
                reservation.setNic(reservationDto.getNic());
            if (reservationDto.getEmail() != null && !reservationDto.getEmail().isEmpty())
                reservation.setEmail(reservationDto.getEmail());
            if (reservationDto.getMobile() != null && !reservationDto.getMobile().isEmpty())
                reservation.setMobile(reservationDto.getMobile());
            if (reservationDto.getLandLine() != null && !reservationDto.getLandLine().isEmpty())
                reservation.setLandLine(reservationDto.getLandLine());
            if (reservationDto.getAddress() != null && !reservationDto.getAddress().isEmpty())
                reservation.setAddress(reservationDto.getAddress());
            if(reservationDto.getEventType() != null && !reservationDto.getEventType().isEmpty())
                reservation.setEventType(reservationDto.getEventType());
            if(reservationDto.getViewers() != null && !reservationDto.getViewers().isEmpty())
                reservation.setViewers(reservationDto.getViewers());
            if(reservationDto.getReservedDate() != null)
                reservation.setReservedDate(reservationDto.getReservedDate());
            if(reservationDto.getEventStartTime() != null)
                reservation.setEventStartTime(reservationDto.getEventStartTime());
            if(reservationDto.getEventEndTime() != null)
                reservation.setEventEndTime(reservationDto.getEventEndTime());
            if(reservationDto.getEventNoOfHours() != null)
                reservation.setEventNoOfHours(reservationDto.getEventNoOfHours());
            if(reservationDto.getSecurity() != null && !reservationDto.getSecurity().isEmpty())
                reservation.setSecurity(reservationDto.getSecurity());
            reservation.setOtherEventType(reservationDto.getOtherEventType());
            reservation.setConcertType(reservationDto.getConcertType());
            reservation.setMusicBand(reservationDto.getMusicBand());
            reservation.setSingers(reservationDto.getSingers());
            reservation.setSpecialInvitees(reservationDto.getSpecialInvitees());
            reservation.setEventAdditionalHours(reservationDto.getEventAdditionalHours());
            reservation.setDecorationStartTime(reservationDto.getDecorationStartTime());
            reservation.setDecorationEndTime(reservationDto.getDecorationEndTime());
            reservation.setDecorationNoOfHours(reservationDto.getDecorationNoOfHours());
            reservation.setRehearsalStartTime(reservationDto.getRehearsalStartTime());
            reservation.setRehearsalEndTime(reservationDto.getRehearsalEndTime());
            reservation.setRehearsalNoOfHours(reservationDto.getRehearsalNoOfHours());
            reservation.setStageLighting(reservationDto.getStageLighting());
            reservation.setStageSoundAdministration(reservationDto.getStageSoundAdministration());
            reservation.setStageDecoration(reservationDto.getStageDecoration());
            reservation.setElectricGenerator(reservationDto.getElectricGenerator());
            reservation.setTicketSalesAtPremises(reservationDto.getTicketSalesAtPremises());
            reservation.setApprovalStatus(ApprovalStatus.PENDING);

            //set advance fee
            reservation.setAdvanceFee(BigDecimal.valueOf(100000));

            //set refundable fee and hall reservation fee
            switch (reservationDto.getEventType()) {
                case "Stage Drama", "Conferences/Lectures", "Awards/Tributes/Ceremonies", "Other" -> {
                    reservation.setRefundableFee(BigDecimal.valueOf(250000));
                    reservation.setHallReservationFee(reservationDto.getEventNoOfHours() == 4 ? BigDecimal.valueOf(350000) : BigDecimal.valueOf(750000));
                }
                case "Musical concerts" -> {
                    reservation.setRefundableFee(BigDecimal.valueOf(1000000));
                    reservation.setHallReservationFee(reservationDto.getEventNoOfHours() == 4 ? BigDecimal.valueOf(500000) : BigDecimal.valueOf(750000));
                }
                default -> throw new IllegalArgumentException("Invalid event type");
            }

            //calculate additional hour fee
            reservation.setAdditionalHourFee(reservationDto.getEventAdditionalHours() != null && reservationDto.getEventAdditionalHours() > 0
                    ? BigDecimal.valueOf(reservationDto.getEventAdditionalHours() * 100000)
                    : BigDecimal.ZERO);

            //calculate decoration fee
            reservation.setDecorationFee(reservationDto.getDecorationNoOfHours() != null && reservationDto.getDecorationNoOfHours() > 0
                    ? BigDecimal.valueOf(reservationDto.getDecorationNoOfHours() * 7500)
                    : BigDecimal.ZERO);

            //calculate rehearsal fee
            reservation.setRehearsalFee(reservationDto.getRehearsalNoOfHours() != null && reservationDto.getRehearsalNoOfHours() > 0
                    ? BigDecimal.valueOf(reservationDto.getRehearsalNoOfHours() * 20000)
                    : BigDecimal.ZERO);

            //set total fee
            reservation.setTotalFee(reservation.getHallReservationFee()
                    .add(reservation.getRefundableFee())
                    .add(reservation.getDecorationFee())
                    .add(reservation.getRehearsalFee())
                    .add(reservation.getAdditionalHourFee()));

            reservationRepository.save(reservation);

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void updateStatus(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()-> new IllegalArgumentException("Reservation not found"));

        reservation.setApprovalStatus(ApprovalStatus.APPROVED);
        reservationRepository.save(reservation);

        //send email
        emailService.sendApprovalStatusEmail(reservation.getEmail(), reservation.getApplicantName(), reservation.getApprovalStatus());

        //send notifications
        String redirectUrl = "/payment/";
        notificationService.createNotification("Your reservation has approved and make the advance fee to confirm the reservation",
                reservationId,
                redirectUrl);

    }

    @Override
    public List<Reservation> getReservationsByUsername(String username) {
        LocalDate date = LocalDate.now();
        return reservationRepository.findUpcomingReservationsByUserName(username,date);
    }

    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.isCancelled()) {
            throw new IllegalStateException("Reservation already cancelled");
        }

        LocalDate today = LocalDate.now();
        LocalDate programDate = reservation.getReservedDate();
        long daysBetween = ChronoUnit.DAYS.between(today, programDate);
        BigDecimal cancellationFee = calculateCancellationFee(reservation.getAdvanceFee(), daysBetween);

        reservation.setCancelled(true);
        reservation.setCancellationFee(cancellationFee);

        reservationRepository.save(reservation);
    }

    private BigDecimal calculateCancellationFee(BigDecimal advanceFee, long daysBetween) {
        BigDecimal cancellationFee;

        if (daysBetween >= 60) {
            // 2 months or more, 5% of advance fee
            cancellationFee = advanceFee.multiply(BigDecimal.valueOf(0.05));
        } else if (daysBetween >= 14) {
            // Between 14 days and 2 months, 25% of advance fee
            cancellationFee = advanceFee.multiply(BigDecimal.valueOf(0.25));
        } else                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   {
            // Less than 14 days, 50% of advance fee
            cancellationFee = advanceFee.multiply(BigDecimal.valueOf(0.50));
        }

        return cancellationFee.setScale(2, RoundingMode.HALF_UP);
    }

}
