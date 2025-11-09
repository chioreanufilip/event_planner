package com.event_planner.event_planner.repository;
import com.event_planner.event_planner.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.event_planner.event_planner.model.Event;
import java.util.List;


@Repository
public interface MediaRepo extends JpaRepository<Media, Integer> {
    public
    List<Media> getMediaByEvent(Event event);
//    List<Media> getB
}
