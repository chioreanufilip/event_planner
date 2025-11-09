package com.event_planner.event_planner.model;
import com.event_planner.event_planner.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
//@Table(name = "user")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("participant")
public class Participant extends User {

}
