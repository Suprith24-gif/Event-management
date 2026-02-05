package com.sup.event_management.controller;

import com.sup.event_management.dto.request.TicketBookRequestDTO;
import com.sup.event_management.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookTickets(
            @RequestBody TicketBookRequestDTO dto) {
        return ticketService.bookTickets(dto);
    }

    @PutMapping("/{ticketId}/cancel")
    public ResponseEntity<?> cancelTicket(
            @PathVariable Long ticketId) {
        return ticketService.cancelTicket(ticketId);
    }
}
