package com.sup.event_management.service;

import com.sup.event_management.entity.Event;
import com.sup.event_management.entity.User;
import com.sup.event_management.repository.EventRepository;
import com.sup.event_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createEvent(Event event) {
        // Check if organizer exists
        Optional<User> organizerOpt = userRepository.findById(event.getOrganizer().getId());
        if (organizerOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("Status", "Organizer not found with ID: " + event.getOrganizer().getId());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // Set the organizer to the existing user entity (to avoid detached entity error)
        event.setOrganizer(organizerOpt.get());

        // availableSeats will be set automatically by @PrePersist, but in case of update, handle carefully
        if (event.getAvailableSeats() == null) {
            event.setAvailableSeats(event.getTotalSeats());
        }

        Event savedEvent = eventRepository.save(event);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getEventById(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("Status", "Event not found with ID: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventOpt.get(), HttpStatus.OK);
    }

    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    public ResponseEntity<?> updateEvent(Long id, Event updatedEvent) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("Status", "Event not found with ID: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Event existingEvent = eventOpt.get();

        // Update fields (except id, createdAt)
        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setEventDate(updatedEvent.getEventDate());
        existingEvent.setTotalSeats(updatedEvent.getTotalSeats());
        existingEvent.setAvailableSeats(updatedEvent.getAvailableSeats());
        existingEvent.setTicketPrice(updatedEvent.getTicketPrice());

        if (updatedEvent.getOrganizer() != null) {
            Optional<User> organizerOpt = userRepository.findById(updatedEvent.getOrganizer().getId());
            if (organizerOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("Status", "Organizer not found with ID: " + updatedEvent.getOrganizer().getId());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            existingEvent.setOrganizer(organizerOpt.get());
        }

        Event savedEvent = eventRepository.save(existingEvent);
        return new ResponseEntity<>(savedEvent, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteEvent(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("Status", "Event not found with ID: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        eventRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
