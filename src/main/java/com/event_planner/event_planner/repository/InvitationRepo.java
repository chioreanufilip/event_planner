package com.event_planner.event_planner.repository;

import com.event_planner.event_planner.model.Invitation;
import com.event_planner.event_planner.model.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepo extends JpaRepository<Invitation, Integer> {

    Optional<Invitation> findByToken(String token);

    // Prevent duplicate pending invites for the same event+email
    boolean existsByEvent_IdAndEmailAndStatus(Integer eventId,
                                              String email,
                                              InvitationStatus status);
}
