package com.coachpad.dto;

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
    private int order;
    private String name;
    private String notes;
    private Long duration;
    private String fieldType;
    
    // ✅ DONNÉES TACTIQUES (JSON hérité du mobile)
    private String tacticalData;
    private String animationData;
    private String thumbnailBase64;
    
    private boolean isLocked;
    private boolean isAnimated;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
}
