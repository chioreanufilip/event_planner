package com.event_planner.event_planner.service;

import com.event_planner.event_planner.model.*;
import com.event_planner.event_planner.repository.EventRepo;
import com.event_planner.event_planner.repository.InvitationRepo;
import com.event_planner.event_planner.repository.RepoParticipant;
import com.event_planner.event_planner.repository.RepoUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Service
public class InvitationService {

    private final EventRepo eventRepo;
    private final RepoParticipant participantRepo;
    private final RepoUser userRepo;
    private final InvitationRepo invitationRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.invite.base-url:http://localhost:4200/invite}")
    private String inviteBaseUrl;

    @Value("${app.invite.ttl-hours:168}")
    private long ttlHours;

    @Value("${app.frontend.login-url:http://localhost:4200/login}")
    private String loginUrl;

    public InvitationService(EventRepo eventRepo,
                             RepoParticipant participantRepo,
                             RepoUser userRepo,
                             InvitationRepo invitationRepo,
                             EmailService emailService,
                             PasswordEncoder passwordEncoder) {
        this.eventRepo = eventRepo;
        this.participantRepo = participantRepo;
        this.userRepo = userRepo;
        this.invitationRepo = invitationRepo;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String sendInvitation(Integer eventId, String email) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        if (invitationRepo.existsByEvent_IdAndEmailAndStatus(eventId, email, InvitationStatus.PENDING)) {
            throw new IllegalStateException("An invitation is already pending for this email and event.");
        }

        Participant participant = participantRepo.findByEmail(email).orElse(null);

        Instant expiresAt = Instant.now().plus(ttlHours, ChronoUnit.HOURS);
        Invitation inv = invitationRepo.save(Invitation.pending(event, participant, email, expiresAt));

        String link = inviteBaseUrl + "?token=" + inv.getToken();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT);
        String when = event.getDate() != null ? df.format(event.getDate()) : "TBA";
        String where = event.getLocation() != null ? event.getLocation() : "TBA";

        String subject = "You're invited: " + event.getName();
        String body = """
                Hello,

                You are invited to: %s
                When: %s
                Where: %s

                Please respond here:
                %s

                This link expires on: %s
                """.formatted(
                event.getName(), when, where, link, expiresAt.toString()
        );

        emailService.sendInvite(email, subject, body);
        return link;
    }

    @Transactional
    public void accept(String token) {
        Invitation inv = invitationRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (inv.isExpired()) {
            inv.setStatus(InvitationStatus.EXPIRED);
            invitationRepo.save(inv);
            throw new IllegalStateException("Invitation expired");
        }

        inv.setStatus(InvitationStatus.ACCEPTED);
        inv.setRespondedAt(Instant.now());

        // Link existing Participant if present; otherwise auto-create a Participant account
        if (inv.getParticipant() == null) {
            userRepo.findByEmail(inv.getEmail()).ifPresentOrElse(existing -> {
                if (existing instanceof Participant p) {
                    inv.setParticipant(p);
                } // if it's Organizer or other type, we leave participant null to avoid role clashes
            }, () -> {
                String tempPassword = generateTempPassword(12);
                Participant p = new Participant();
                p.setEmail(inv.getEmail());
                p.setName(inv.getEmail()); // adjust if you collect real name later
                p.setPassword(passwordEncoder.encode(tempPassword));
                participantRepo.save(p);
                inv.setParticipant(p);

                // Notify user of their new account + temp password
                String subject = "Your Event Planner account is ready";
                String body = """
                        Hello,

                        We've created a Participant account for your invitation email: %s
                        Temporary password: %s

                        Please log in and change your password:
                        %s
                        """.formatted(inv.getEmail(), tempPassword, loginUrl);
                emailService.sendInvite(inv.getEmail(), subject, body);
            });
        }

        invitationRepo.save(inv);
    }

    @Transactional
    public void decline(String token) {
        Invitation inv = invitationRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (inv.isExpired()) {
            inv.setStatus(InvitationStatus.EXPIRED);
        } else {
            inv.setStatus(InvitationStatus.DECLINED);
            inv.setRespondedAt(Instant.now());
        }
        invitationRepo.save(inv);
    }

    @Transactional(readOnly = true)
    public Invitation getByToken(String token) {
        return invitationRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }

    private static String generateTempPassword(int len) {
        final String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@$%";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        return sb.toString();
    }
}
