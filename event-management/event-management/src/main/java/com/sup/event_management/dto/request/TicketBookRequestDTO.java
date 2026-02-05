package com.sup.event_management.dto.request;

import lombok.Data;

@Data
public class TicketBookRequestDTO {
    private Long userId;
    private Long eventId;
    private int seatCount;
}
