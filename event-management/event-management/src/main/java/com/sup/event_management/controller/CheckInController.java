package com.sup.event_management.controller;

import com.sup.event_management.service.CheckInService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/{ticketId}")
    public ResponseEntity<?> checkIn(@PathVariable Long ticketId) {
        return checkInService.checkIn(ticketId);
    }

    @PostMapping("/qr")
    public ResponseEntity<?> checkInWithQR(@RequestBody String qrCodeJson) {
        return checkInService.checkInByQRCode(qrCodeJson);
    }
}
