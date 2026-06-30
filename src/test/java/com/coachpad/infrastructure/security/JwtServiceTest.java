package com.coachpad.infrastructure.security;

import com.coachpad.infrastructure.persistance.postgresql.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    void generateAndValidateToken() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("test@test.com")
                .password("encoded")
                .build();

        String token = jwtService.generateToken(user);
        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo("test@test.com");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }
}