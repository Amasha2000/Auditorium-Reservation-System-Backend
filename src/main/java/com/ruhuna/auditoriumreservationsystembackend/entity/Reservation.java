package com.ruhuna.reservationsystembackend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruhuna.reservationsystembackend.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservation_id;
    private String organizationName;
    private String applicantName;
    private String nic;
    private String email;
    private String mobile;
    private String landLine;
    private String address;
    private String eventType;
    private String otherEventType;
    private String concertType;
    private String musicBand;
    private String singers;
    private String specialInvitees;
    private String viewers;
    @Enumerated(value = EnumType.STRING)
    private ApprovalStatus approvalStatus;
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservedDate;
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime eventStartTime;
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime eventEndTime;
    @Column(nullable = false)
    private Integer eventNoOfHours;
    private Integer eventAdditionalHours;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime decorationStartTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime decorationEndTime;
    private Integer decorationNoOfHours;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime rehearsalStartTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime rehearsalEndTime;
    private Integer rehearsalNoOfHours;
    private String stageLighting;
    private String stageSoundAdministration;
    private String electricGenerator;
    private String stageDecoration;
    private String ticketSalesAtPremises;
    private String security;
    @Column(precision = 10, scale = 2)
    private BigDecimal advanceFee;
    @Column(precision = 10, scale = 2)
    private BigDecimal refundableFee;
    @Column(precision = 10, scale = 2)
    private BigDecimal totalFee;
    @Column(precision = 10, scale = 2)
    private BigDecimal cancellationFee;
    @JsonProperty
    @JsonInclude
    private boolean isCancelled;
    @Column(precision = 10, scale = 2)
    private BigDecimal hallReservationFee;
    @Column(precision = 10, scale = 2)
    private BigDecimal decorationFee;
    @Column(precision = 10, scale = 2)
    private BigDecimal rehearsalFee;
    @Column(precision = 10, scale = 2)
    private BigDecimal additionalHourFee;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private com.ruhuna.reservationsystembackend.entity.Admin admin;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private com.ruhuna.reservationsystembackend.entity.GuestUser user;
    @ManyToOne
    @JoinColumn(name = "vc_id")
    private VC vc;
    @OneToMany(mappedBy = "reservation")
    private List<Payment> paymentList;
}
