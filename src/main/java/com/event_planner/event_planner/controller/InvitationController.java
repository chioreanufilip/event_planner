package com.event_planner.event_planner.controller;

import com.event_planner.event_planner.model.Invitation;
import com.event_planner.event_planner.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService service;

    public InvitationController(InvitationService service) {
        this.service = service;
    }

    public static record InviteRequest(String email) {}

    @PostMapping("/events/{eventId}/send")
    public ResponseEntity<String> sendInvite(@PathVariable Integer eventId, @RequestBody InviteRequest req) {
        String link = service.sendInvitation(eventId, req.email());
        return ResponseEntity.ok(link);
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
