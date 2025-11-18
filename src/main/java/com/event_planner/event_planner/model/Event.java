package com.event_planner.event_planner.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_event")
    private int id;

    @Column(name = "name")
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "date")
    private Date date;
    @Column(name = "location")
    private String location;
    @Column(name = "budget")
    private BigDecimal budget;
    @Column(name = "size")
    private Integer size = 0;
    @ManyToOne
    @JoinColumn(name="host_user_id",nullable = false)
    private Organizer hostOrganizer;



}
