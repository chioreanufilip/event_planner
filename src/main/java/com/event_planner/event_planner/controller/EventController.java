package com.event_planner.event_planner.controller;
import com.event_planner.event_planner.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.event_planner.event_planner.service.EventService;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody Event event, Authentication authentication) {
        try{
            Event createdEvent = eventService.createEvent(event, authentication);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        }
        catch (AccessDeniedException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @RequestBody Event eventData,
            Authentication authentication) {

        try {
            // 2. Trimite ID-ul, noile date ȘI user-ul la service
            Event updatedEvent = eventService.updateEvent(id, eventData, authentication);
            return ResponseEntity.ok(updatedEvent);

        } catch (AccessDeniedException e) {
            // 3. Dacă service-ul zice "nu ai voie"
            return new ResponseEntity<>("You are not the organizer for this event.", HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            // 4. Dacă service-ul zice "nu l-am găsit"
            return new ResponseEntity<>("Event not found.", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id, Authentication authentication) {
        try{
            boolean foundDeleted = eventService.getEvent(id,authentication).isPresent();
            if (foundDeleted) {
                eventService.deleteEvent(id,authentication);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (AccessDeniedException e){
            return new ResponseEntity<>("You do not have permission to delete this",HttpStatus.FORBIDDEN);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>("Event not found.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id, Authentication authentication) {
        try{
            Event event = eventService.getEvent(id,authentication).get();
            return new ResponseEntity<>(event, HttpStatus.OK);
        }
        catch (AccessDeniedException e){
            return new ResponseEntity<>("You do not have permission as you are not organizer",HttpStatus.FORBIDDEN);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>("Event not found.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/my-events")
    public ResponseEntity<?> getAllEvents(Authentication authentication) {
        try{
            return new ResponseEntity<>(eventService.getAllEventsbyOrganizer(authentication), HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("You are not the organizer for this event.", HttpStatus.FORBIDDEN);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>("Event not found.", HttpStatus.NOT_FOUND);
        }
    }


}
