package com.event_planner.event_planner.service;

import com.event_planner.event_planner.model.User;
import com.event_planner.event_planner.repository.RepoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final RepoUser repoUser;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(RepoUser repoUser, PasswordEncoder passwordEncoder) {
        this.repoUser = repoUser;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public <T extends User> T createUser(T user) {
        String plainPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        user.setPassword(encodedPassword);
        return repoUser.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return repoUser.findByEmail(email);
    }
    public Optional<User> findUserById(Long id) {
        return repoUser.findById(id.intValue());
    }
    public void deleteUserById(Long id) {
        repoUser.deleteById(id.intValue());
    }
    public List<User> findAll(){
        List<User> listOfUsers = repoUser.findAll();
        return listOfUsers;
    }
    @Transactional
    public User updateUser(Integer id, User userWithUpdates) {
        // 1. Folosești UserRepository, care știe de TOATE tipurile de User
        Optional<User> existingUserOpt = repoUser.findById(id);

        if (existingUserOpt.isEmpty()) {
            return null;
        }

        // 2. Aici, existingUser poate fi un Participant SAU un Organizer
        User existingUser = existingUserOpt.get();

        // 3. Setezi DOAR câmpurile comune, definite în clasa User
        existingUser.setPassword(userWithUpdates.getPassword());
        existingUser.setEmail(userWithUpdates.getEmail());
        existingUser.setName(userWithUpdates.getName());

        // 4. Salvezi. JPA știe ce tip e (Participant/Organizer) și face UPDATE corect
        return repoUser.save(existingUser);
    }
}
