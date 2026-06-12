package com.coachpad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class SceneDTO {
    private String id;
    private String projectId;

    @JsonProperty("orderIndex")
    private int order;

    private String name;
    private String notes;

    @JsonProperty("durationMs")
    private Long duration;

    private String fieldType;
    
    // ✅ DONNÉES TACTIQUES (JSON hérité du mobile)
    private String tacticalData;
    private String animationData;
    private String thumbnailBase64;
    
    @JsonProperty("isLocked")
    private boolean isLocked;

    @JsonProperty("isAnimated")
    private boolean isAnimated;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
}
