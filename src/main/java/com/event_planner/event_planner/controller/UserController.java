package com.event_planner.event_planner.controller;

import com.event_planner.event_planner.model.Organizer;
import com.event_planner.event_planner.model.Participant;
import com.event_planner.event_planner.model.User;
import com.event_planner.event_planner.repository.RepoUser;
import com.event_planner.event_planner.service.UserService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
//    private final RepoUser repoUser;

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        // 1. Luăm email-ul din token (care e validat de Spring)
        String email = authentication.getName();

        // 2. Căutăm user-ul și îl returnăm
        return userService.findUserByEmail(email)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/register/participant")
//    public ResponseEntity<Participant> registerParticipant(@RequestBody Participant user) {
//        Participant savedUser = userService.createUser(user);
//        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
//    }
//
//    @PostMapping("/register/organizer")
//    public ResponseEntity<Organizer> registerOrganizer(@RequestBody Organizer user) {
//        Organizer savedUser = userService.createUser(user);
//        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
//    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<User> getUserbyEmail(@PathVariable String email) {
        return userService.findUserByEmail(email).map(user->new ResponseEntity<>(user, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserbyId(@PathVariable Long id) {
        return userService.findUserById(id).map(participant->new ResponseEntity<>(participant, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        boolean foundDeleted = userService.findUserById(id).isPresent();
        userService.deleteUserById(id);

        if (foundDeleted) {
            // if it corectly deletes it
            return ResponseEntity.noContent().build();
        } else {
            // if it doesn t exist
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/participant/{id_user}")
    public ResponseEntity<Participant> updateParticipant(@PathVariable Long id_user, @RequestBody Participant participant) {
        Participant savedParticipant = (Participant) userService.updateUser(id_user.intValue(),participant);

        if (savedParticipant == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(savedParticipant, HttpStatus.OK);
    }
    @PutMapping("/organizer/{id_user}")
    public ResponseEntity<Organizer> updateOrganizer(@PathVariable Long id_user, @RequestBody Organizer participant) {
        Organizer savedParticipant = (Organizer) userService.updateUser(id_user.intValue(),participant);

        if (savedParticipant == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(savedParticipant, HttpStatus.OK);
    }
}
