package com.event_planner.event_planner.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
//@Table(name = "user")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("organizer")
public class Organizer extends User{
}
