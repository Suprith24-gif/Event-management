package com.sup.event_management.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String title;

        @Column(length = 1000)
        private String description;

        @Column(nullable = false)
        private String location;

        @Column(nullable = false)
        private LocalDateTime eventDate;

        @Column(nullable = false)
        private Integer totalSeats;

        @Column(nullable = false)
        private Integer availableSeats;

        @Column(nullable = false)
        private Double ticketPrice;

        @ManyToOne
        @JoinColumn(name = "organizer_id", nullable = false)
        private User organizer;

        private LocalDateTime createdAt;

        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now();
            this.availableSeats = this.totalSeats;
        }

        // getters and setters
}


