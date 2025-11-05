package com.event_planner.event_planner.service;

import com.event_planner.event_planner.model.User;
import com.event_planner.event_planner.repository.RepoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    private final RepoUser repoUser;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(RepoUser repoUser, PasswordEncoder passwordEncoder) {
        this.repoUser = repoUser;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        String plainPassword = user.getPasscode();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        user.setPasscode(encodedPassword);
        return repoUser.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return repoUser.findByEmail(email);
    }
}
