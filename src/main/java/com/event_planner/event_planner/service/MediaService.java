package com.event_planner.event_planner.service;
import com.event_planner.event_planner.model.*;
import com.event_planner.event_planner.repository.EventRepo;
import com.event_planner.event_planner.repository.MediaRepo;
import com.event_planner.event_planner.repository.RepoUser;
import com.cloudinary.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final MediaRepo mediaRepo;
    private final RepoUser repoUser;
    private final EventRepo eventRepo;
    private final Cloudinary cloudinary;

    public Media createMedia(Long eventId,Media media, Authentication authentication) throws AccessDeniedException {
        String username = authentication.getName();
        User organizer =  repoUser.findByEmail(username).orElseThrow(()-> new RuntimeException("Organizer not found"));
        if (organizer instanceof Organizer) {
            if (eventRepo.getEventByHostOrganizer((Organizer) repoUser.findByEmail(username).get()).contains(eventRepo.findById(eventId.intValue()).get())){
                media.setEvent(eventRepo.findById(eventId.intValue()).get());
                return mediaRepo.save(media);
            }
            throw new AccessDeniedException("Organizer doesn t have this event");
        }
        else  {
            throw new AccessDeniedException("You are not an organizer");
        }

    }

    @Transactional
    public void deleteMedia(Long mediaId, Authentication authentication) throws AccessDeniedException {
        String requesterEmail = authentication.getName();
//        System.out.println(mediaRepo.findById(mediaId.intValue()).get().getUrl());
        if (mediaRepo.findById(mediaId.intValue()).isPresent()) {
            System.out.println(mediaRepo.findById(mediaId.intValue()).get().getUrl());
            Media mediaToDelete = mediaRepo.findById(mediaId.intValue()).get();

//                .orElseThrow(() -> new RuntimeException("Media not found"));
            Organizer eventOwner = mediaToDelete.getEvent().getHostOrganizer();

            // 4. *** VERIFICAREA DE SECURITATE ***
            // Verificăm dacă persoana care face cererea (requester)
            // ESTE proprietarul evenimentului.
            if (!eventOwner.getEmail().equals(requesterEmail)) {
                // Dacă nu e, aruncăm eroarea.
                throw new AccessDeniedException("You are not the organizer of this event.");
            }
            Map<String, String> options = new HashMap<>();

            // 4. Folosim tipul salvat în baza de date!
            if (mediaToDelete.getMediaType() == MediaType.VIDEO) {
                options.put("resource_type", "video");
            } else {
                options.put("resource_type", "image"); // Default e 'image'
            }
            try {
//                System.out.println(extractPublicIdFromUrl(mediaToDelete.getUrl()));
                cloudinary.uploader().destroy(extractPublicIdFromUrl(mediaToDelete.getUrl()), options);
//                System.out.println("sau aici");
//                mediaRepo.delete(mediaToDelete);
//                System.out.println("sau poate aici");
            } catch (IOException e) {
//                System.out.println(e.getMessage());
                throw new RuntimeException("Failed to delete from cloudinary"+e.getMessage());
            }
            mediaRepo.delete(mediaToDelete);
        }
        else {
            throw new RuntimeException("Media not found");
        }
    }
        private String extractPublicIdFromUrl (String url){
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }
        public List<Media> getMediaForEvent (Long eventId){
            return mediaRepo.getMediaByEvent(eventRepo.findById(eventId.intValue()).get());

        }
        public List<Media> getMediaForOrganizer (Authentication authentication) throws AccessDeniedException {
            String username = authentication.getName();
            if (repoUser.findByEmail(username).isPresent()) {
//            Long id = repoUser.findByEmail(username).get().getIdUser().longValue();
                List<Event> eventList = eventRepo.getEventByHostOrganizer((Organizer) repoUser.findByEmail(username).get());
                List<Media> mediaList = new ArrayList<>();
                eventList.forEach(event -> mediaList.addAll(mediaRepo.getMediaByEvent(event)));
                return mediaList;
            }
            throw new AccessDeniedException("You are not an organizer");
        }

}

