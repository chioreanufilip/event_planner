package com.event_planner.event_planner.controller;

import com.event_planner.event_planner.model.Participant;
import com.event_planner.event_planner.model.User;
import com.event_planner.event_planner.service.ParticipantService;
import com.event_planner.event_planner.service.UserService;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/participants")
public class ParticipantController {
    private final ParticipantService participantService;

    @Autowired
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody Participant participant) {
        Participant savedParticipant = participantService.createParticipant(participant);
        return new ResponseEntity<>(savedParticipant, HttpStatus.CREATED);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Participant> getParticipantbyEmail(@PathVariable String email) {
        return participantService.findParticipantByEmail(email).map(participant->new ResponseEntity<>(participant, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
    @GetMapping("/{id_user}")
    public ResponseEntity<Participant> getParticipantbyId(@PathVariable Long id_user) {
        return participantService.findParticipantById(id_user).map(participant->new ResponseEntity<>(participant, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id_user}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {

        boolean foundDeleted = participantService.findParticipantById(id).isPresent();
        participantService.deleteParticipant(id);

        if (foundDeleted) {
            // if it corectly deletes it
            return ResponseEntity.noContent().build();
        } else {
            // if it doesn t exist
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id_user}")
    public ResponseEntity<Participant> updateParticipant(@PathVariable Long id_user, @RequestBody Participant participant) {
        Participant savedParticipant = participantService.updateParticipant(id_user.intValue(),participant);

        if (savedParticipant == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(savedParticipant, HttpStatus.OK);
    }
}
