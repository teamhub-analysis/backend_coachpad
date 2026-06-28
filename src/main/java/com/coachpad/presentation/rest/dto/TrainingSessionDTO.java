package com.coachpad.presentation.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class TrainingSessionDTO {
    private String id;
    private String name;
    private String description;
    private String parentId;
    private LocalDateTime date;
    private String timeSlot;
    private Integer sessionNumber;
    private int intensity;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private boolean isFavorite;
    private boolean isArchived;
}
