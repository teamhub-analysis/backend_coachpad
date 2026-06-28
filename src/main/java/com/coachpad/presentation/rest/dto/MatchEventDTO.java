package com.coachpad.presentation.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class MatchEventDTO {
    private String id;
    private String parentId;
    private String opponentName;
    private LocalDateTime matchDate;
    private String location;
    private String competition;
    private Integer homeScore;
    private Integer awayScore;
    private String homeTeamId;
    private String awayTeamId;
    private String homeTeamName;
    private String awayTeamName;
    private String analysisNotes;
    private String videoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private boolean isFavorite;
    private boolean isArchived;
}
