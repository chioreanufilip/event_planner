package com.event_planner.event_planner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import static org.springframework.security.config.Customizer.withDefaults; // Import static

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors(withDefaults()) // <-- ADAUGĂ ACEASTĂ LINIE
            .csrf(csrf -> csrf.disable()) // Dezactivăm CSRF (nu e necesar pt API-uri stateless)
            .authorizeHttpRequests(auth -> auth
                    // Aici punem rutele publice
                    .requestMatchers("/api/auth/**") // Tot ce e în /api/auth/
                    .permitAll() // este permis pentru toată lumea
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers("/api/media/event/**")
                    .permitAll()

                    // Aici spunem că TOATE celelalte rute
                    .anyRequest()
                    .authenticated() // Necesită autentificare
            )
            //Invitation
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/invitations/**").permitAll()
                    .anyRequest().authenticated()
            )


            // Spunem lui Spring să NU folosească Sesiuni (cookies)
            // Vom folosi token-uri, deci e "stateless"
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider) // Folosim provider-ul din ApplicationConfig
            // Adăugăm filtrul nostru JWT *înainte* de filtrul standard de login
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Setează originile permise (React app)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Setează metodele permise
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Setează headerele permise
        configuration.setAllowedHeaders(List.of("*"));

        // Permite trimiterea de credențiale (token-uri)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplică această configurație pentru toate rutele tale API
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

}