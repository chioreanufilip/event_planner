package com.event_planner.event_planner.repository;
import com.event_planner.event_planner.model.Event;
import com.event_planner.event_planner.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EventRepo extends JpaRepository<Event, Integer> {
    public List<Event> getEventByHostOrganizer(Organizer organizer);
}
