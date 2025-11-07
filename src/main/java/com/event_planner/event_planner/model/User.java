package com.event_planner.event_planner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@Data
@Table(name="user")
@DiscriminatorColumn(name = "dtype")
public abstract class User implements UserDetails {
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
    private String password;
//    @Column(name = "is_participant")
//    private Integer isParticipant;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    // Aici poți returna roluri, de ex. "ROLE_PARTICIPANT"
    // Pentru simplitate, returnăm o listă goală momentan.
    return List.of();
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

//    public User(String name, String email, String password) {
//        this.name = name;
//        this.email = email;
//        this.password = password;
////        this.isParticipant = is_participant;
//    }
}
