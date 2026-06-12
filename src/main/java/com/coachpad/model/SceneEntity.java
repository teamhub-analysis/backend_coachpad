package com.coachpad.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scenes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SceneEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "project_id", nullable = false)
    private String projectId;

    @Column(name = "order_index")
    private int orderIndex;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Long durationMs;

    @Builder.Default
    private String fieldType = "football";

    // ✅ DONNÉES TACTIQUES (JSON)
    // Contient : players, drawings, objects, texts
    @Column(name = "tactical_data", columnDefinition = "TEXT")
    private String tacticalData;

    // ✅ ANIMATIONS (JSON)
    // Contient : SceneAnimation (keyframes, ball passes)
    @Column(name = "animation_data", columnDefinition = "TEXT")
    private String animationData;

    // ✅ MINIATURE (Base64)
    @Column(name = "thumbnail_base64", columnDefinition = "TEXT")
    private String thumbnailBase64;

    @Builder.Default
    private boolean isLocked = false;
    @Builder.Default
    private boolean isAnimated = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;
}
