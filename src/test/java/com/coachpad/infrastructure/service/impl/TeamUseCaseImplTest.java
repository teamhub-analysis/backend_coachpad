package com.coachpad.infrastructure.service.impl;

import com.coachpad.domain.model.TeamModel;
import com.coachpad.domain.repository.TeamRepository;
import com.coachpad.domain.repository.TeamDesignRepository;
import com.coachpad.domain.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamUseCaseImplTest {

    @Mock private TeamRepository teamRepository;
    @Mock private TeamDesignRepository teamDesignRepository;
    @Mock private PlayerRepository playerRepository;
    private TeamUseCaseImpl teamUseCase;

    @BeforeEach
    void setUp() {
        teamUseCase = new TeamUseCaseImpl(teamRepository, teamDesignRepository, playerRepository);
    }

    @Test
    void getAllTeams_ShouldReturnAllTeams() {
        when(teamRepository.getAllTeams()).thenReturn(List.of(new TeamModel()));

        List<TeamModel> result = teamUseCase.getAllTeams();

        assertThat(result).hasSize(1);
    }
}