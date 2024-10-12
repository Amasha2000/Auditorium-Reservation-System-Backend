package com.ruhuna.reservationsystembackend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruhuna.reservationsystembackend.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payment_id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate paymentDate;
    @JsonFormat(pattern = "HH:mm:ss")
    @Column(nullable = false)
    private LocalTime paymentTime;
    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private com.ruhuna.reservationsystembackend.entity.Reservation reservation;
}
