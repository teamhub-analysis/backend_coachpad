package com.coachpad.dto;

import com.coachpad.model.enums.ProjectCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ProjectDTO {
    private String id;
    private String name;
    private String description;
    private ProjectCategory category;
    private String parentId;
    private boolean isFavorite;
    private boolean isArchived;
    private boolean isTemplate;
    
    // Dates & Types
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime matchDate;
    private String weekType;

    // Tactical Info
    private String objectif;
    private String organisation;
    private String consignes;
    private String variantes;

    // Statistics
    private int sceneCount;
    private int playerCount;
    private int homePlayerCount;
    private int awayPlayerCount;
    private int objectCount;
    private int drawingCount;
    private int totalDurationSeconds;
    private boolean hasAnimations;

    // View Stats
    private int viewCount;
    private int modificationCount;
    private int commentsCount;
    private LocalDateTime lastViewed;
    private LocalDateTime lastExport;

    // Global Content
    private String formation;
    private String homeTeamId;
    private String awayTeamId;
    private String homeTeamName;
    private String awayTeamName;

    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    
    private List<String> exerciseIds;
    private List<String> sessionIds;
    private List<SceneDTO> scenes;
}
