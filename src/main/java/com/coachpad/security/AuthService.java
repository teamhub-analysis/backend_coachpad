package com.coachpad.security;

import com.coachpad.dto.AuthRequest;
import com.coachpad.dto.AuthResponse;
import com.coachpad.dto.RegisterRequest;
import com.coachpad.persistence.entity.UserEntity;
import com.coachpad.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);


        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public AuthResponse register(RegisterRequest request) {
                // Check if email already exists
                if (repository.findByEmail(request.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("Email already in use");
                }

                var user = UserEntity.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .build();
                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return AuthResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        public AuthResponse authenticate(AuthRequest request) {
                log.info("Attempting to authenticate user: {}", request.getEmail());
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));
                        log.info("Authentication successful for user: {}", request.getEmail());
                } catch (Exception e) {
                        log.error("Authentication failed for user: {}. Error: {}", request.getEmail(), e.getMessage());
                        throw e;
                }

                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow(() -> {
                                        log.error("User not found in repository after successful authentication: {}", request.getEmail());
                                        return new UsernameNotFoundException("User not found after authentication");
                                });

                var jwtToken = jwtService.generateToken(user);
                log.info("Token generated successfully for user: {}", request.getEmail());
                
                return AuthResponse.builder()
                                .token(jwtToken)
                                .build();
        }

}
