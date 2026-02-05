package com.sup.event_management.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "CheckIn")
@Data
public class CheckIn {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "ticket_id", nullable = false, unique = true)
        private Ticket ticket;

        private LocalDateTime checkInTime;

        @PrePersist
        protected void onCheckIn() {
            this.checkInTime = LocalDateTime.now();
        }
}
