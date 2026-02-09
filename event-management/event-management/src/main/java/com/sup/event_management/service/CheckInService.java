package com.sup.event_management.service;

import com.sup.event_management.entity.CheckIn;
import com.sup.event_management.entity.Ticket;
import com.sup.event_management.entity.TicketStatus;
import com.sup.event_management.exceptions.AppException;
import com.sup.event_management.exceptions.ExceptionSeverity;
import com.sup.event_management.exceptions.ExceptionType;
import com.sup.event_management.repository.CheckInRepository;
import com.sup.event_management.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CheckInService {

    private static final Logger logger = LoggerFactory.getLogger(CheckInService.class);

    private final CheckInRepository checkInRepository;
    private final TicketRepository ticketRepository;

    private static final long EARLY_CHECKIN_MINUTES = 1;
    private static final long LATE_CHECKIN_HOURS = 2;

    public CheckInService(CheckInRepository checkInRepository,
                          TicketRepository ticketRepository) {
        this.checkInRepository = checkInRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public ResponseEntity<?> checkIn(Long ticketId) {
        logger.info("Check-in requested for Ticket ID: {}", ticketId);
        Ticket ticket = getValidatedTicket(ticketId, null);
        return doCheckIn(ticket);
    }

    @Transactional
    public ResponseEntity<?> checkInByQRCode(String qrCodeJson) {
        logger.info("Check-in via QR code requested. QR Data: {}", qrCodeJson);
        try {
            Map<String, String> qrData = parseQRCodeJson(qrCodeJson);

            Long ticketId = Long.valueOf(qrData.get("ticketId"));
            String ticketCode = qrData.get("ticketCode");

            Ticket ticket = getValidatedTicket(ticketId, ticketCode);
            return doCheckIn(ticket);
        } catch (Exception e) {
            logger.warn("Invalid QR code during check-in: {}", e.getMessage());
            throw new AppException(
                    "Invalid QR code",
                    ExceptionType.VALIDATION,
                    ExceptionSeverity.INFO,
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
    }

    private Map<String, String> parseQRCodeJson(String qrCodeJson) {
        logger.debug("Parsing QR code JSON: {}", qrCodeJson);
        qrCodeJson = qrCodeJson.trim();
        qrCodeJson = qrCodeJson.substring(1, qrCodeJson.length() - 1); // remove { }
        Map<String, String> map = new HashMap<>();
        for (String entry : qrCodeJson.split(",")) {
            String[] kv = entry.split(":");
            String key = kv[0].trim().replaceAll("\"", "");
            String value = kv[1].trim().replaceAll("\"", "");
            map.put(key, value);
        }
        logger.debug("Parsed QR code map: {}", map);
        return map;
    }

    private Ticket getValidatedTicket(Long ticketId, String ticketCode) {
        logger.info("Validating Ticket ID: {} with code: {}", ticketId, ticketCode);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    logger.warn("Ticket not found: {}", ticketId);
                    return new AppException(
                            "Ticket not found",
                            ExceptionType.RESOURCE_NOT_FOUND,
                            ExceptionSeverity.WARNING,
                            HttpStatus.NOT_FOUND,
                            "Ticket ID : " + ticketId
                    );
                });

        if (ticketCode != null && !ticket.getTicketCode().equals(ticketCode)) {
            logger.warn("Invalid ticket QR code for Ticket ID: {}", ticketId);
            throw new AppException(
                    "Invalid ticket QR code",
                    ExceptionType.VALIDATION,
                    ExceptionSeverity.INFO,
                    HttpStatus.BAD_REQUEST,
                    "Ticket ID : " + ticketId
            );
        }

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            logger.info("Attempted check-in for cancelled Ticket ID: {}", ticketId);
            throw new AppException(
                    "Cancelled ticket cannot be checked in",
                    ExceptionType.BUSINESS,
                    ExceptionSeverity.INFO,
                    HttpStatus.BAD_REQUEST,
                    "Ticket ID : " + ticketId
            );
        }

        if (ticket.getStatus() == TicketStatus.CHECKED_IN) {
            logger.info("Attempted check-in for already checked-in Ticket ID: {}", ticketId);
            throw new AppException(
                    "Ticket already checked in",
                    ExceptionType.BUSINESS,
                    ExceptionSeverity.INFO,
                    HttpStatus.BAD_REQUEST,
                    "Ticket ID : " + ticketId
            );
        }

        LocalDateTime eventStart = ticket.getEvent().getEventDate();
        LocalDateTime checkInStart = eventStart.minusMinutes(EARLY_CHECKIN_MINUTES);
        LocalDateTime checkInEnd = eventStart.plusHours(LATE_CHECKIN_HOURS);
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(checkInStart)) {
            logger.info("Check-in too early for Ticket ID: {}. Check-in starts at {}", ticketId, checkInStart);
            throw new AppException(
                    "Event \"" + ticket.getEvent().getTitle() + "\" has not started yet. You can check in from " + checkInStart,
                    ExceptionType.VALIDATION,
                    ExceptionSeverity.INFO,
                    HttpStatus.BAD_REQUEST,
                    "Event ID : " + ticket.getEvent().getId()
            );
        }

        if (now.isAfter(checkInEnd)) {
            logger.info("Check-in period ended for Ticket ID: {}. Event ended at {}", ticketId, checkInEnd);
            throw new AppException(
                    "Event \"" + ticket.getEvent().getTitle() + "\" check-in period has ended.",
                    ExceptionType.VALIDATION,
                    ExceptionSeverity.WARNING,
                    HttpStatus.BAD_REQUEST,
                    "Event ID : " + ticket.getEvent().getId()
            );
        }

        logger.info("Ticket ID: {} validated successfully", ticketId);
        return ticket;
    }

    // Perform check-in
    private ResponseEntity<?> doCheckIn(Ticket ticket) {
        logger.info("Performing check-in for Ticket ID: {}", ticket.getId());
        ticket.setStatus(TicketStatus.CHECKED_IN);

        CheckIn checkIn = new CheckIn();
        checkIn.setTicket(ticket);
        checkInRepository.save(checkIn);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Check-in successful");
        response.put("ticketId", ticket.getId());
        response.put("ticketCode", ticket.getTicketCode());
        response.put("checkInTime", checkIn.getCheckInTime());

        logger.info("Check-in successful for Ticket ID: {}", ticket.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
