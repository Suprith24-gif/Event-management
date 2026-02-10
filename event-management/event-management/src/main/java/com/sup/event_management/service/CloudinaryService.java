package com.sup.event_management.service;

import com.sup.event_management.entity.MediaType;
import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Upload an image
    public Map uploadImage(MultipartFile file, String folder) {
        try {
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    // Upload a video
    public Map uploadVideo(MultipartFile file, String folder) {
        try {
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", folder,
                            "resource_type", "video"
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Video upload failed", e);
        }
    }

    // Delete media by public_id
    public void delete(String publicId, MediaType type) {
        try {
            cloudinary.uploader().destroy(
                    publicId,
                    Map.of("resource_type", type == MediaType.VIDEO ? "video" : "image")
            );
        } catch (Exception e) {
            throw new RuntimeException("Media deletion failed", e);
        }
    }
}
