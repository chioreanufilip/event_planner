package com.event_planner.event_planner.controller;
import com.event_planner.event_planner.model.Media;
import com.event_planner.event_planner.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    /**
     * Endpoint pentru a încărca o poză/video nou.
     * React trimite { "url": "...", "mediaType": "PHOTO" }
     * Este securizat: trebuie să fii logat ȘI să fii proprietarul evenimentului.
     */
    @PostMapping("/event/{eventId}")
    public ResponseEntity<?> createMedia(
            @PathVariable Long eventId,
            @RequestBody Media mediaData,
            Authentication authentication
    ) {
        try {
            Media savedMedia = mediaService.createMedia(eventId, mediaData, authentication);
            return new ResponseEntity<>(savedMedia, HttpStatus.CREATED);

        } catch (AccessDeniedException e) {
            // Aruncată dacă user-ul nu e proprietarul evenimentului
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            // Aruncată dacă evenimentul nu e găsit
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint pentru a șterge o poză/video.
     * Este securizat: trebuie să fii proprietarul evenimentului.
     */
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<?> deleteMedia(
            @PathVariable Long mediaId,
            Authentication authentication
    ) {
        try {
            mediaService.deleteMedia(mediaId, authentication);
            return ResponseEntity.noContent().build(); // Status 204 (Șters cu succes)

        } catch (AccessDeniedException e) {
            // Aruncată dacă nu ești proprietarul
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            // Aruncată dacă media nu e găsită
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            // Aruncată dacă ștergerea de pe Cloudinary eșuează
            return new ResponseEntity<>("Eroare la ștergerea de pe cloud.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint PUBLIC pentru a vedea galeria unui eveniment.
     * Oricine poate face asta, nu e nevoie de Authentication.
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Media>> getMediaForEvent(@PathVariable Long eventId) {
        List<Media> mediaList = mediaService.getMediaForEvent(eventId);
        return ResponseEntity.ok(mediaList);
    }

    /**
     * Endpoint PRIVAT pentru un organizator, ca să vadă
     * toată media de la toate evenimentele sale.
     */
    @GetMapping("/my-media")
    public ResponseEntity<?> getMyMedia(Authentication authentication) {
        try {
            List<Media> mediaList = mediaService.getMediaForOrganizer(authentication);
            return ResponseEntity.ok(mediaList);

        } catch (AccessDeniedException e) {
            // Aruncată dacă un Participant încearcă să acceseze
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
