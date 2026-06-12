package com.coachpad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class MesocycleDTO {
    private String id;
    private String name;
    private String description;
    private String parentId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String phase;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private boolean isFavorite;
    private boolean isArchived;
}
