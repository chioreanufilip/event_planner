package com.event_planner.event_planner.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
//@Data
@Table(name="user")
public class User {
    @Id
    @Column(name="idUser")
//    private Long idUser;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;

    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "passcode")
    private String passcode;

}
