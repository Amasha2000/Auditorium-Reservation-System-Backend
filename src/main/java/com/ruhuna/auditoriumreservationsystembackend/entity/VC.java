package com.ruhuna.reservationsystembackend.entity;

import com.ruhuna.reservationsystembackend.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class VC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vc_id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    @Enumerated(value = EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "vc")
    private List<com.ruhuna.reservationsystembackend.entity.Reservation> reservationList;
    @OneToMany(mappedBy = "vc")
    private List<Notification> notificationList;
}
