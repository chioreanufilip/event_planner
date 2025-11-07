package com.event_planner.event_planner.repository;
import com.event_planner.event_planner.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepoParticipant extends JpaRepository<Participant, Integer> {
    Optional<Participant> findByEmail(String email);
//    Optional<Participant> findById(Long id);
}
