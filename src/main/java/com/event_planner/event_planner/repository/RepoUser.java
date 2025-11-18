package com.event_planner.event_planner.repository;

import com.event_planner.event_planner.model.Organizer;
import com.event_planner.event_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RepoUser extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("SELECT o FROM Organizer o WHERE o.email = :email")
    Optional<Organizer> findOrganizerByEmail(@Param("email") String email);
}
