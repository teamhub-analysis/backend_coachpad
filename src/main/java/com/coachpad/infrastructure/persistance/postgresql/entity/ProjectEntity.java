package com.coachpad.infrastructure.persistance.postgresql.entity;

import com.coachpad.infrastructure.persistance.postgresql.entity.SceneEntity;
import com.coachpad.domain.model.enums.ProjectCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * EntitÃ© de base pour la hiÃ©rarchie Coach-Pad.
 * Supporte le lien parent-enfant via parentId.
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectCategory category;

    @Column(name = "parent_id")
    private String parentId;

    @Builder.Default
    @Column(name = "is_template")
    private boolean isTemplate = false;

    @Builder.Default
    @Column(name = "is_favorite")
    private boolean isFavorite = false;

    @Builder.Default
    @Column(name = "is_archived")
    private boolean isArchived = false;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime matchDate;
    private String weekType;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private int intensity = 0;
    private String timeSlot;
    private Integer microcycleNumber;
    private Integer sessionNumber;
    private String opponentName;

    @Column(columnDefinition = "TEXT")
    private String thumbnailBase64;

    @Column(columnDefinition = "TEXT")
    private String objectif;
    @Column(columnDefinition = "TEXT")
    private String organisation;
    @Column(columnDefinition = "TEXT")
    private String consignes;
    @Column(columnDefinition = "TEXT")
    private String variantes;

    @Builder.Default
    private int sceneCount = 0;
    @Builder.Default
    private int playerCount = 0;
    @Builder.Default
    private int homePlayerCount = 0;
    @Builder.Default
    private int awayPlayerCount = 0;
    @Builder.Default
    private int objectCount = 0;
    @Builder.Default
    private int drawingCount = 0;
    @Builder.Default
    private int totalDurationSeconds = 0;
    @Builder.Default
    private boolean hasAnimations = false;

    @Builder.Default
    private int viewCount = 0;
    @Builder.Default
    private int modificationCount = 0;
    @Builder.Default
    private int commentsCount = 0;
    private LocalDateTime lastViewed;
    private LocalDateTime lastExport;

    private String formation;
    private String homeTeamId;
    private String awayTeamId;
    private String homeTeamName;
    private String awayTeamName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "project_exercise_links", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "exercise_id")
    private List<String> exerciseIds = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "project_session_links", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "session_id")
    private List<String> sessionIds = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    @OrderBy("orderIndex ASC")
    private List<SceneEntity> scenes = new ArrayList<>();
}
