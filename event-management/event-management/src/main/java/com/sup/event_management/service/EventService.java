package com.sup.event_management.service;

import com.sup.event_management.dto.response.PagedResponse;
import com.sup.event_management.entity.Event;
import com.sup.event_management.entity.User;
import com.sup.event_management.exceptions.AppException;
import com.sup.event_management.exceptions.ExceptionSeverity;
import com.sup.event_management.exceptions.ExceptionType;
import com.sup.event_management.repository.EventRepository;
import com.sup.event_management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createEvent(Event event) {
        logger.info("Creating event: {}", event.getTitle());

        User organizer = userRepository.findById(event.getOrganizer().getId())
                .orElseThrow(() -> {
                    logger.warn("Organizer not found with ID: {}", event.getOrganizer().getId());
                    return new AppException(
                            "Organizer not found",
                            ExceptionType.RESOURCE_NOT_FOUND,
                            ExceptionSeverity.WARNING,
                            HttpStatus.NOT_FOUND,
                            "Organizer ID : " + event.getOrganizer().getId()
                    );
                });

        event.setOrganizer(organizer);

        if (event.getAvailableSeats() == null) {
            event.setAvailableSeats(event.getTotalSeats());
        }

        Event savedEvent = eventRepository.save(event);
        logger.info("Event created successfully with ID: {}", savedEvent.getId());
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getEventById(Long id) {
        logger.info("Fetching event by ID: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Event not found with ID: {}", id);
                    return new AppException(
                            "Event not found",
                            ExceptionType.RESOURCE_NOT_FOUND,
                            ExceptionSeverity.INFO,
                            HttpStatus.NOT_FOUND,
                            "Event ID : " + id
                    );
                });

        logger.info("Event fetched successfully: {}", event.getTitle());
        return ResponseEntity.ok(event);
    }

    public ResponseEntity<PagedResponse<Event>> getAllEvents(int page, int size) {
        logger.info("Fetching all events, page: {}, size: {}", page, size);

        List<Event> allEvents = eventRepository.findAll();
        int start = page * size;
        int end = Math.min(start + size, allEvents.size());
        List<Event> pagedEvents = allEvents.subList(
                Math.min(start, allEvents.size()),
                Math.min(end, allEvents.size())
        );

        PagedResponse<Event> response = new PagedResponse<>(
                pagedEvents,
                page,
                size,
                allEvents.size(),
                (int) Math.ceil((double) allEvents.size() / size)
        );

        logger.info("Fetched {} events out of total {}", pagedEvents.size(), allEvents.size());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateEvent(Long id, Event updatedEvent) {
        logger.info("Updating event with ID: {}", id);

        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Event not found with ID: {}", id);
                    return new AppException(
                            "Event not found",
                            ExceptionType.RESOURCE_NOT_FOUND,
                            ExceptionSeverity.WARNING,
                            HttpStatus.NOT_FOUND,
                            "Event ID : " + id
                    );
                });

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setEventDate(updatedEvent.getEventDate());
        existingEvent.setTotalSeats(updatedEvent.getTotalSeats());
        existingEvent.setAvailableSeats(updatedEvent.getAvailableSeats());
        existingEvent.setTicketPrice(updatedEvent.getTicketPrice());

        if (updatedEvent.getOrganizer() != null) {
            User organizer = userRepository.findById(updatedEvent.getOrganizer().getId())
                    .orElseThrow(() -> {
                        logger.warn("Organizer not found with ID: {}", updatedEvent.getOrganizer().getId());
                        return new AppException(
                                "Organizer not found",
                                ExceptionType.RESOURCE_NOT_FOUND,
                                ExceptionSeverity.WARNING,
                                HttpStatus.NOT_FOUND,
                                "Organizer ID : " + updatedEvent.getOrganizer().getId()
                        );
                    });
            existingEvent.setOrganizer(organizer);
        }

        Event savedEvent = eventRepository.save(existingEvent);
        logger.info("Event updated successfully with ID: {}", savedEvent.getId());
        return ResponseEntity.ok(savedEvent);
    }

    public ResponseEntity<?> deleteEvent(Long id) {
        logger.info("Deleting event with ID: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Event not found with ID: {}", id);
                    return new AppException(
                            "Event not found",
                            ExceptionType.RESOURCE_NOT_FOUND,
                            ExceptionSeverity.CRITICAL,
                            HttpStatus.NOT_FOUND,
                            "Event ID : " + id
                    );
                });

        eventRepository.delete(event);
        logger.info("Event deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
