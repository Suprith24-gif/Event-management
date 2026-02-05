package com.sup.event_management.dto.response;

import com.sup.event_management.entity.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketResponseDTO {
    private Long ticketId;
    private String ticketCode;
    private TicketStatus status;
    private LocalDateTime bookedAt;
    private Long eventId;
    private Long userId;
    private String userName;
}
