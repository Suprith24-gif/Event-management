package com.sup.event_management.service;

import com.sup.event_management.entity.EventMedia;
import com.sup.event_management.entity.MediaType;
import com.sup.event_management.exceptions.AppException;
import com.sup.event_management.exceptions.ExceptionSeverity;
import com.sup.event_management.exceptions.ExceptionType;
import com.sup.event_management.repository.EventMediaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class EventMediaService {

    private final EventMediaRepository eventMediaRepository;
    private final CloudinaryService cloudinaryService;

    public EventMediaService(EventMediaRepository eventMediaRepository, CloudinaryService cloudinaryService) {
        this.eventMediaRepository = eventMediaRepository;
        this.cloudinaryService = cloudinaryService;
    }

    // Upload image
    public EventMedia uploadImage(Long eventId, MultipartFile file, boolean profile) {
        long imageCount = eventMediaRepository.countByEventIdAndMediaType(eventId, MediaType.IMAGE);

        if (!profile && imageCount >= 5) {
            throw new AppException(
                    "Cannot upload more than 5 images for this event",
                    ExceptionType.BUSINESS,
                    ExceptionSeverity.WARNING,
                    HttpStatus.BAD_REQUEST,
                    "Event ID: " + eventId
            );
        }

        Map uploadResult = cloudinaryService.uploadImage(file, "events/" + eventId);

        EventMedia media = new EventMedia();
        media.setEventId(eventId);
        media.setUrl((String) uploadResult.get("secure_url"));
        media.setPublicId((String) uploadResult.get("public_id"));
        media.setMediaType(MediaType.IMAGE);
        media.setProfile(profile);

        // If uploading a new profile, remove old profile
        if (profile) {
            eventMediaRepository.findByEventIdAndProfileTrue(eventId).ifPresent(oldProfile -> {
                cloudinaryService.delete(oldProfile.getPublicId(), MediaType.IMAGE);
                eventMediaRepository.delete(oldProfile);
            });
        }

        return eventMediaRepository.save(media);
    }

    // Upload video
    public EventMedia uploadVideo(Long eventId, MultipartFile file) {
        if (file.getSize() > 1024L * 1024L * 1024L) { // 1 GB
            throw new AppException(
                    "Video size exceeds 1GB limit",
                    ExceptionType.BUSINESS,
                    ExceptionSeverity.WARNING,
                    HttpStatus.BAD_REQUEST,
                    "Event ID: " + eventId
            );
        }

        Map uploadResult = cloudinaryService.uploadVideo(file, "events/" + eventId);

        EventMedia media = new EventMedia();
        media.setEventId(eventId);
        media.setUrl((String) uploadResult.get("secure_url"));
        media.setPublicId((String) uploadResult.get("public_id"));
        media.setMediaType(MediaType.VIDEO);
        media.setProfile(false);

        return eventMediaRepository.save(media);
    }

    // Delete media
    public void deleteMedia(Long mediaId) {
        EventMedia media = eventMediaRepository.findById(mediaId)
                .orElseThrow(() -> new AppException(
                        "Media not found",
                        ExceptionType.RESOURCE_NOT_FOUND,
                        ExceptionSeverity.WARNING,
                        HttpStatus.NOT_FOUND,
                        "Media ID: " + mediaId
                ));

        cloudinaryService.delete(media.getPublicId(), media.getMediaType());
        eventMediaRepository.delete(media);
    }

    // Get all media for an event
    public List<EventMedia> getMediaForEvent(Long eventId) {
        return eventMediaRepository.findByEventId(eventId);
    }
}
