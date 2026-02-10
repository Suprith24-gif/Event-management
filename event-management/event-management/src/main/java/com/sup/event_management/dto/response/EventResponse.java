package com.sup.event_management.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private Integer totalSeats;
    private Integer availableSeats;
    private Double ticketPrice;
    private Long organizerId;

    private String profileImageUrl;      // Event profile image
    private List<String> imageUrls;      // Other event images
    private String videoUrl;             // Event video
}
