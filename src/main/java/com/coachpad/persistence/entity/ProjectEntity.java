package com.coachpad.persistence.entity;

import com.coachpad.model.SceneEntity;
import com.coachpad.model.enums.ProjectCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité de base pour la hiérarchie Coach-Pad.
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
    @Column(length = 50) // On accepte les IDs "timestamp" du mobile ou des IDs générés
    private String id;

    // Owner of the project — used to filter projects by authenticated user
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

    @Column(name = "is_template")
    private boolean isTemplate = false;

    @Column(name = "is_favorite")
    private boolean isFavorite = false;

    @Column(name = "is_archived")
    private boolean isArchived = false;

    // Métadonnées de contenu (Microcycle / Session / Exercise)
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime matchDate;
    private String weekType;

    @ElementCollection
    @CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    // Periodization Metadata
    private int intensity = 0;
    private String timeSlot;
    private Integer microcycleNumber;
    private Integer sessionNumber;
    private String opponentName;

    @Column(columnDefinition = "TEXT")
    private String thumbnailBase64;

    // Instructions tactiques (Exercise)
    @Column(columnDefinition = "TEXT")
    private String objectif;
    @Column(columnDefinition = "TEXT")
    private String organisation;
    @Column(columnDefinition = "TEXT")
    private String consignes;
    @Column(columnDefinition = "TEXT")
    private String variantes;

    // Statistiques & Compteurs
    private int sceneCount = 0;
    private int playerCount = 0;
    private int homePlayerCount = 0;
    private int awayPlayerCount = 0;
    private int objectCount = 0;
    private int drawingCount = 0;
    private int totalDurationSeconds = 0;
    private boolean hasAnimations = false;

    // Stats de consultation
    private int viewCount = 0;
    private int modificationCount = 0;
    private int commentsCount = 0;
    private LocalDateTime lastViewed;
    private LocalDateTime lastExport;

    // Contenu Global (Équipes & Formation)
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

    // --- Relations Optionnelles ---

    // Un projet peut avoir plusieurs IDs d'exercices liés (Session -> Exercises)
    @ElementCollection
    @CollectionTable(name = "project_exercise_links", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "exercise_id")
    private List<String> exerciseIds = new ArrayList<>();

    // Un projet peut avoir plusieurs IDs de séances liés (Microcycle -> Sessions)
    @ElementCollection
    @CollectionTable(name = "project_session_links", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "session_id")
    private List<String> sessionIds = new ArrayList<>();

    // --- Tactical Content ---
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    @OrderBy("orderIndex ASC")
    private List<SceneEntity> scenes = new ArrayList<>();
}
