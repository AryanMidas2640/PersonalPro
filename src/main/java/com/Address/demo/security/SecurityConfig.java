package com.Address.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint entryPoint;

    public SecurityConfig(JwtFilter jwtFilter,
                          JwtAuthenticationEntryPoint entryPoint) {
        this.jwtFilter = jwtFilter;
        this.entryPoint = entryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                // Disable CSRF
                .csrf(csrf -> csrf.disable())

                // Disable CORS temp
                .cors(cors -> {})

                // Exception handler
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(entryPoint)
                )

                // Stateless session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization Rules
                .authorizeHttpRequests(auth -> auth

                        // ======================
                        // PUBLIC APIs
                        // ======================
                        .requestMatchers("/api/jobs/login").permitAll()
                        .requestMatchers("/api/jobs/signing").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        // ======================
                        // RECRUITER ONLY
                        // ======================
                        .requestMatchers("/api/jobs/add").hasAuthority("RECRUITER")
                        .requestMatchers("/api/jobs/update/**").hasAuthority("RECRUITER")
                        .requestMatchers("/api/jobs/delete/**").hasAuthority("RECRUITER")
                        .requestMatchers("/api/jobs/my-applicants").hasAuthority("RECRUITER")

                        // ======================
                        // STUDENT ONLY
                        // ======================
                        .requestMatchers("/api/jobs/apply/**").hasAuthority("STUDENT")
                        .requestMatchers("/api/jobs/my-applied").hasAuthority("STUDENT")
                        .requestMatchers("/api/student/**").hasAuthority("STUDENT")

                        // ======================
                        // BOTH
                        // ======================
                        .requestMatchers(HttpMethod.GET, "/api/jobs/all")
                        .hasAnyAuthority("STUDENT", "RECRUITER")

                        .requestMatchers(HttpMethod.GET, "/api/jobs/{jobId}")
                        .hasAnyAuthority("STUDENT", "RECRUITER")

                        .requestMatchers(HttpMethod.GET, "/api/jobs/city/**")
                        .hasAnyAuthority("STUDENT", "RECRUITER")

                        .requestMatchers(HttpMethod.GET, "/api/jobs/tenant/**")
                        .hasAnyAuthority("STUDENT", "RECRUITER")

                        .requestMatchers(HttpMethod.GET, "/api/resume/parse")
                        .hasAnyAuthority("STUDENT", "RECRUITER")

                        // Any other request
                        .anyRequest().authenticated()
                )

                .httpBasic(httpBasic -> httpBasic.disable());

        // JWT Filter
        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}