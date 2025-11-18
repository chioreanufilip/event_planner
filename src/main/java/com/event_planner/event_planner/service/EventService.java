package com.event_planner.event_planner.service;
import com.event_planner.event_planner.model.Event;
import com.event_planner.event_planner.model.Organizer;
import com.event_planner.event_planner.model.Participant;
import com.event_planner.event_planner.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.event_planner.event_planner.repository.EventRepo;
import com.event_planner.event_planner.repository.RepoUser;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepo eventRepo;
    private final RepoUser repoUser;

    public Event createEvent(Event event,Authentication authentication ) throws AccessDeniedException {
        String username = authentication.getName();
        
        System.out.println("=== EVENT CREATION DEBUG ===");
        System.out.println("Username from auth: " + username);
        
        // Try to find as Organizer directly
        Optional<Organizer> organizerOpt = repoUser.findOrganizerByEmail(username);
        
        System.out.println("Organizer found: " + organizerOpt.isPresent());
        
        if (organizerOpt.isPresent()) {
            Organizer organizer = organizerOpt.get();
            System.out.println("Organizer ID: " + organizer.getIdUser());
            event.setHostOrganizer(organizer);
            Event saved = eventRepo.save(event);
            System.out.println("Event saved with ID: " + saved.getId());
            return saved;
        }
        else  {
            System.out.println("ERROR: No organizer found with email: " + username);
            throw new AccessDeniedException("User is not an organizer or not found: " + username);
        }
    }

    public Event updateEvent(Long id,Event event,Authentication authentication ) throws AccessDeniedException {
        String username = authentication.getName();
        User organizer =  repoUser.findByEmail(username).orElseThrow(()-> new RuntimeException("Organizer not found"));
        if(organizer instanceof Organizer && organizer.getIdUser().equals(eventRepo.findById(id.intValue()).get().getHostOrganizer().getIdUser())) {
            Optional<Event> optionalEvent = eventRepo.findById(id.intValue());
            if (optionalEvent.isEmpty()) {
                return null;
            }
            Event updatedEvent = optionalEvent.get();
//            updatedEvent.setHostOrganizer(event.getHostOrganizer());
            updatedEvent.setName(event.getName());
            updatedEvent.setDate(event.getDate());
            ;
            updatedEvent.setBudget(event.getBudget());
            updatedEvent.setSize(event.getSize());
            updatedEvent.setLocation(event.getLocation());
            return eventRepo.save(updatedEvent);
        }
        else {
            throw new AccessDeniedException("You are organizer of this event");
        }
    }
    public Optional<Event> getEvent(Long id,Authentication authentication ) throws AccessDeniedException {
        String username = authentication.getName();
        User organizer =  repoUser.findByEmail(username).orElseThrow(()-> new RuntimeException("Organizer not found"));
        if (organizer instanceof Organizer) {
            return eventRepo.findById(id.intValue());
        }
        throw new AccessDeniedException("You are not an organizer");

    }
    public List<Event> getAllEventsbyOrganizer(Authentication authentication) throws AccessDeniedException {
        String username = authentication.getName();
        User organizer =  repoUser.findByEmail(username).orElseThrow(()-> new RuntimeException("Organizer not found"));
        if(organizer instanceof Organizer) {
            return eventRepo.getEventByHostOrganizer((Organizer) repoUser.findByEmail(authentication.getName()).get());
        }
        throw new AccessDeniedException("You not organizer");
    }
    public void deleteEvent(Long id,Authentication authentication ) throws AccessDeniedException {
        String username = authentication.getName();
        User organizer =  repoUser.findByEmail(username).orElseThrow(()-> new RuntimeException("Organizer not found"));
        if (organizer instanceof Organizer) {
            eventRepo.deleteById(id.intValue());
        }
        else {
            throw new AccessDeniedException("You are not an organizer");
        }
    }
}
