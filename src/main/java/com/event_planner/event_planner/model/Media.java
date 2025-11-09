package com.event_planner.event_planner.model;
import com.event_planner.event_planner.model.MediaType;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "photos")
public class Media {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String url;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @ManyToOne
    @JoinColumn(name = "id_event",nullable = false)
    private Event event;
}
