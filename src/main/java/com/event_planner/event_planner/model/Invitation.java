package com.event_planner.event_planner.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invitations", indexes = {
        @Index(name = "idx_inv_token", columnList = "token", unique = true)
})
@Data
@NoArgsConstructor
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // Optional link if a Participant with the same email already exists
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant respondedAt;

    public static Invitation pending(Event event, Participant participant, String email, Instant expiresAt) {
        Invitation i = new Invitation();
        i.setToken(UUID.randomUUID().toString().replace("-", ""));
        i.setEvent(event);
        i.setParticipant(participant);
        i.setEmail(email);
        i.setExpiresAt(expiresAt);
        i.setStatus(InvitationStatus.PENDING);
        return i;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
