package com.coachpad.presentation.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class DrillDTO {
    private String id;
    private String name;
    private String description;
    private String parentId;
    private String category;
    private int rpe;
    private int durationMinutes;
    private int minPlayers;
    private int maxPlayers;
    private List<String> equipment;
    private String objectif;
    private String organisation;
    private String consignes;
    private String variantes;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private boolean isFavorite;
    private boolean isArchived;
}
