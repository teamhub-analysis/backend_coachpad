package com.coachpad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // désactive CSRF
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // autorise toutes les requêtes sans authentification
            )
            .formLogin(form -> form.disable()) // désactive le formulaire de login
            .httpBasic(basic -> basic.disable()); // désactive l’authentification basique

        return http.build();
    }
}
