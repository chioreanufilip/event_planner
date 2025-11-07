package com.event_planner.event_planner.service;

import com.event_planner.event_planner.model.Participant;
import com.event_planner.event_planner.repository.RepoParticipant;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
//import com.event_planner.event_planner.model.Participant;
import java.util.Optional;

@Service
public class ParticipantService {
    private final RepoParticipant repoParticipant;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ParticipantService(RepoParticipant repoParticipant, PasswordEncoder passwordEncoder) {
        this.repoParticipant= repoParticipant;
        this.passwordEncoder = passwordEncoder;
    }

    public Participant createParticipant(Participant participant) {
        String plainPassword = participant.getPassword();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        participant.setPassword(encodedPassword);
        return repoParticipant.save(participant);
    }

    public Optional<Participant> findParticipantByEmail(String email) {
        return repoParticipant.findByEmail(email);
    }
    public Optional<Participant> findParticipantById(Long id) {
        return repoParticipant.findById(id.intValue());
    }

    public void deleteParticipant(Long id) {
        repoParticipant.deleteById(id.intValue());
    }

    @Transactional
    public Participant updateParticipant(Integer id,Participant participant) {
        Optional<Participant> existingParticipantOpt = repoParticipant.findById(id);
        if (existingParticipantOpt.isEmpty()) {
            return null;
        }
        Participant existingParticipant = existingParticipantOpt.get();
        existingParticipant.setPassword(participant.getPassword());
        existingParticipant.setEmail(participant.getEmail());
        existingParticipant.setName(participant.getName());
        return repoParticipant.save(existingParticipant);
    }

}
