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

    @Column(name = "participant_name")
    private String participantName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant respondedAt;

    /**
     * Create a pending invitation with participant name support
     * 
     * @param event The event to invite to
     * @param participant Existing participant (can be null for new people)
     * @param email Email address to send invitation to
     * @param participantName Name of the person (can be null)
     * @param expiresAt When the invitation expires
     * @return A new pending invitation
     */
    public static Invitation pending(Event event, Participant participant, String email, String participantName, Instant expiresAt) {
        Invitation i = new Invitation();
        i.setToken(UUID.randomUUID().toString().replace("-", ""));
        i.setEvent(event);
        i.setParticipant(participant);
        i.setEmail(email);
        
        // Priority: provided name > participant's name > "Guest"
        if (participantName != null && !participantName.trim().isEmpty()) {
            i.setParticipantName(participantName);
        } else if (participant != null && participant.getName() != null) {
            i.setParticipantName(participant.getName());
        } else {
            i.setParticipantName("Guest");
        }
        
        i.setExpiresAt(expiresAt);
        i.setStatus(InvitationStatus.PENDING);
        return i;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}