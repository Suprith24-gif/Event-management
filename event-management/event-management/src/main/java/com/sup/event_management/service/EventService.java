package com.sup.event_management.service;

import com.sup.event_management.entity.Event;
import com.sup.event_management.entity.User;
import com.sup.event_management.exceptions.AppException;
import com.sup.event_management.exceptions.ExceptionSeverity;
import com.sup.event_management.exceptions.ExceptionType;
import com.sup.event_management.repository.EventRepository;
import com.sup.event_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createEvent(Event event) {

        User organizer = userRepository.findById(event.getOrganizer().getId())
                .orElseThrow(() -> new AppException(
                        "Organizer not found",
                        ExceptionType.RESOURCE_NOT_FOUND,
                        ExceptionSeverity.WARNING,
                        HttpStatus.NOT_FOUND,
                        "Organizer ID : " + event.getOrganizer().getId()
                ));

        event.setOrganizer(organizer);

        if (event.getAvailableSeats() == null) {
            event.setAvailableSeats(event.getTotalSeats());
        }

        Event savedEvent = eventRepository.save(event);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getEventById(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        "Event not found",
                        ExceptionType.RESOURCE_NOT_FOUND,
                        ExceptionSeverity.INFO,
                        HttpStatus.NOT_FOUND,
                        "Event ID : " + id
                ));

        return ResponseEntity.ok(event);
    }

    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());
    }

    public ResponseEntity<?> updateEvent(Long id, Event updatedEvent) {

        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        "Event not found",
                        ExceptionType.RESOURCE_NOT_FOUND,
                        ExceptionSeverity.WARNING,
                        HttpStatus.NOT_FOUND,
                        "Event ID : " + id
                ));

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setEventDate(updatedEvent.getEventDate());
        existingEvent.setTotalSeats(updatedEvent.getTotalSeats());
        existingEvent.setAvailableSeats(updatedEvent.getAvailableSeats());
        existingEvent.setTicketPrice(updatedEvent.getTicketPrice());

        if (updatedEvent.getOrganizer() != null) {
            User organizer = userRepository.findById(updatedEvent.getOrganizer().getId())
                    .orElseThrow(() -> new AppException(
                            "Organizer not found",
                            ExceptionType.RESOURCE_NOT_FOUND,
                            ExceptionSeverity.WARNING,
                            HttpStatus.NOT_FOUND,
                            "Organizer ID : " + updatedEvent.getOrganizer().getId()
                    ));
            existingEvent.setOrganizer(organizer);
        }

        Event savedEvent = eventRepository.save(existingEvent);
        return ResponseEntity.ok(savedEvent);
    }

    public ResponseEntity<?> deleteEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        "Event not found",
                        ExceptionType.RESOURCE_NOT_FOUND,
                        ExceptionSeverity.CRITICAL,
                        HttpStatus.NOT_FOUND,
                        "Event ID : " + id
                ));

        eventRepository.delete(event);
        return ResponseEntity.noContent().build();
    }
}
