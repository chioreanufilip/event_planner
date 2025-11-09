package com.event_planner.event_planner.controller;

import com.event_planner.event_planner.controller.AuthResponse;
import com.event_planner.event_planner.controller.LoginRequest;
import com.event_planner.event_planner.model.Organizer;
import com.event_planner.event_planner.model.Participant;
import com.event_planner.event_planner.model.User;
import com.event_planner.event_planner.service.JwtService;
import com.event_planner.event_planner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Ruta de bază pentru tot ce e legat de autentificare
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public record LoginRequest(String email, String password) {}
    public record UserDto(Long id, String email, String name,String role) {}
    public record AuthResponse(String token,UserDto user) {}
    // Mută rutele de register aici

    @PostMapping("/register/participant")
    public ResponseEntity<Participant> registerParticipant(@RequestBody Participant user) {
        Participant savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/register/organizer")
    public ResponseEntity<Organizer> registerOrganizer(@RequestBody Organizer user) {
        Organizer savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Endpoint-ul de LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // 1. Spring verifies the password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 2. Dacă e ok, luăm user-ul
        var user = (User) authentication.getPrincipal();

        // 3. Generăm un token
        String token = jwtService.generateToken(user);
        UserDto userDto = new UserDto(user.getIdUser().longValue(),user.getEmail(),user.getName(),(user instanceof Organizer) ? "organizer" : "participant");

        // 4. Returnăm token-ul
        return ResponseEntity.ok(new AuthResponse(token,userDto));
    }
}
