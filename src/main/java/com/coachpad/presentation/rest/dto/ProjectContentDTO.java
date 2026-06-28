package com.coachpad.presentation.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ProjectContentDTO {
    private String projectId;
    private List<SceneDTO> scenes;
    private String formation;
    private String homeTeamId;
    private String awayTeamId;
    private String homeTeamName;
    private String awayTeamName;
    private List<String> exerciseIds;
    private List<String> sessionIds;
}
