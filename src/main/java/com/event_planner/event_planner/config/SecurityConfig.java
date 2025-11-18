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
            // CORS configuration for cross-origin requests (React frontend)
            .cors(withDefaults())
            
            // DISABLE CSRF - Required for REST APIs with JWT authentication
            // CSRF is only needed for cookie-based authentication, not JWT
            .csrf(csrf -> csrf.disable())
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                    // Public endpoints - No authentication required
                    .requestMatchers("/api/auth/**").permitAll()          // Login, register
                    .requestMatchers("/api/invitations/*/accept").permitAll()   // Accept invitations
                    .requestMatchers("/api/invitations/*/decline").permitAll()  // Decline invitations
                    .requestMatchers("/api/media/event/**").permitAll()   // Public event media
                    .requestMatchers("/health/**").permitAll()            // Health checks
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight
                    
                    // Protected endpoints - Authentication required
                    .anyRequest().authenticated()
            )
            
            // Stateless session - No server-side sessions, only JWT tokens
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Add custom authentication provider and JWT filter
            .authenticationProvider(authenticationProvider)
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