package com.sup.event_management.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String ticketCode;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @ManyToOne
        @JoinColumn(name = "event_id", nullable = false)
        private Event event;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TicketStatus status;

        private LocalDateTime bookedAt;

        @PrePersist
        protected void onCreate() {
            this.ticketCode = UUID.randomUUID().toString();
            this.status = TicketStatus.BOOKED;
            this.bookedAt = LocalDateTime.now();
        }

}
