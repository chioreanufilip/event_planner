package com.event_planner.event_planner.repository;

import com.event_planner.event_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepoUser extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
