package com.event_planner.event_planner.config;

import com.event_planner.event_planner.repository.RepoUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final RepoUser userRepository;

    // Acesta este bean-ul care îi spune lui Spring cum să caute un utilizator
    @Bean
    public UserDetailsService userDetailsService() {
        // Folosim o expresie lambda. Când Spring cere un user,
        // noi îl căutăm în repo-ul nostru după email.
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Acesta este "provider-ul" de autentificare
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService()); // Îi spunem ce serviciu să folosească
        authProvider.setPasswordEncoder(passwordEncoder()); // Îi spunem ce encoder de parole să folosească
        return authProvider;
    }

    // Bean-ul care va fi folosit de AuthController pentru a face login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean-ul pentru criptarea parolelor
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}