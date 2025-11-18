package com.event_planner.event_planner.controller;

import com.event_planner.event_planner.model.Invitation;
import com.event_planner.event_planner.service.ExcelService;
import com.event_planner.event_planner.service.InvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService service;
    private final ExcelService excelService;

    public InvitationController(InvitationService service, ExcelService excelService) {
        this.service = service;
        this.excelService = excelService;
    }

    public static record InviteRequest(String email, String name) {}

    @PostMapping("/events/{eventId}/send")
    public ResponseEntity<String> sendInvite(@PathVariable Integer eventId, @RequestBody InviteRequest req) {
        String link = service.sendInvitation(eventId, req.email(), req.name());
        return ResponseEntity.ok(link);
    }

    /**
     * Bulk import participants from Excel and send invitations
     * POST /api/invitations/events/{eventId}/bulk-import
     * Expects multipart file upload with Excel (.xlsx)
     */
    @PostMapping("/events/{eventId}/bulk-import")
    public ResponseEntity<?> bulkImportAndInvite(
            @PathVariable Integer eventId,
            @RequestParam("file") MultipartFile file) {
        
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("Only .xlsx files are supported");
            }
            
            // Parse Excel file
            List<Map<String, String>> participants = excelService.parseParticipantEmails(file);
            
            if (participants.isEmpty()) {
                return ResponseEntity.badRequest().body("No valid emails found in Excel file");
            }
            
            // Send bulk invitations
            List<String> sentEmails = service.sendBulkInvitations(eventId, participants);
            
            Map<String, Object> response = Map.of(
                "message", "Invitations sent successfully",
                "totalParsed", participants.size(),
                "totalSent", sentEmails.size(),
                "emails", sentEmails
            );
            
            return ResponseEntity.ok(response);
            
        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading Excel file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing invitations: " + e.getMessage());
        }
    }

    @GetMapping("/{token}")
    public ResponseEntity<Invitation> getInvitation(@PathVariable String token) {
        return ResponseEntity.ok(service.getByToken(token));
    }

    @PostMapping("/{token}/accept")
    public ResponseEntity<Void> accept(@PathVariable String token) {
        service.accept(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{token}/decline")
    public ResponseEntity<Void> decline(@PathVariable String token) {
        service.decline(token);
        return ResponseEntity.ok().build();
    }
}
