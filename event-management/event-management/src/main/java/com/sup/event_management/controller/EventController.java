package com.sup.event_management.controller;

import com.sup.event_management.dto.response.PagedResponse;
import com.sup.event_management.entity.Event;
import com.sup.event_management.service.EventService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(value = "/with-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEventWithMedia(
            @RequestPart("event") Event event,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @RequestPart(value = "video", required = false) MultipartFile video
    ) {
        return eventService.createEventWithMedia(event, profileImage, images, video);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<Event>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllEvents(page, size);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id);
    }
}
