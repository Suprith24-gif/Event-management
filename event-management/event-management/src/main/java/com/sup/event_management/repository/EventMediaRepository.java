package com.sup.event_management.repository;

import com.sup.event_management.entity.EventMedia;
import com.sup.event_management.entity.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventMediaRepository extends JpaRepository<EventMedia, Long> {

    // Get all media for a specific event
    List<EventMedia> findByEventId(Long eventId);

    // Count images for an event
    long countByEventIdAndMediaType(Long eventId, MediaType mediaType);

    // Get the profile image for an event
    Optional<EventMedia> findByEventIdAndProfileTrue(Long eventId);
}
