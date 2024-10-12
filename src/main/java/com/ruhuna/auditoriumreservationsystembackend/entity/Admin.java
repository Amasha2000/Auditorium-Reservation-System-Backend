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
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admin_id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    @Enumerated(value = EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "admin")
    private List<Event> eventList;
    @OneToMany(mappedBy = "admin")
    private List<Reservation> reservationList;
    @OneToMany(mappedBy = "admin")
    private List<Notification> notificationList;
}
