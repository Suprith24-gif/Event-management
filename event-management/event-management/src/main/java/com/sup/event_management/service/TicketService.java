package com.sup.event_management.service;

import com.sup.event_management.dto.request.TicketBookRequestDTO;
import com.sup.event_management.dto.response.TicketResponseDTO;
import com.sup.event_management.entity.Event;
import com.sup.event_management.entity.Ticket;
import com.sup.event_management.entity.TicketStatus;
import com.sup.event_management.entity.User;
import com.sup.event_management.repository.EventRepository;
import com.sup.event_management.repository.TicketRepository;
import com.sup.event_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public ResponseEntity<?> bookTickets(TicketBookRequestDTO dto) {

        if (dto.getSeatCount() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Seat count must be greater than zero");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔒 Pessimistic lock on event row
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() < dto.getSeatCount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Not enough seats available");
        }

        event.setAvailableSeats(
                event.getAvailableSeats() - dto.getSeatCount()
        );

        List<Ticket> tickets = new ArrayList<>();

        for (int i = 0; i < dto.getSeatCount(); i++) {
            Ticket ticket = new Ticket();
            ticket.setUser(user);
            ticket.setEvent(event);
            tickets.add(ticket);
        }
        ticketRepository.saveAll(tickets);

        List<TicketResponseDTO> response =
                tickets.stream()
                        .map(ticket -> toDTO(ticket))
                        .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional
    public ResponseEntity<?> cancelTicket(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            return ResponseEntity.badRequest()
                    .body("Ticket already cancelled");
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        Event event = ticket.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + 1);

        return ResponseEntity.ok("Ticket cancelled successfully with ticket ID : "+ticketId);
    }

    private TicketResponseDTO toDTO(Ticket ticket) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setTicketId(ticket.getId());
        dto.setTicketCode(ticket.getTicketCode());
        dto.setStatus(ticket.getStatus());
        dto.setBookedAt(ticket.getBookedAt());
        dto.setEventId(ticket.getEvent().getId());
        dto.setUserId(ticket.getUser().getId());
        dto.setUserName(ticket.getUser().getName());
        return dto;
    }
}
