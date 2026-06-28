package com.coachpad.infrastructure.persistance.postgresql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players", indexes = {
    @Index(name = "idx_player_team", columnList = "team_id"),
    @Index(name = "idx_player_number_team", columnList = "number, team_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"team"})
@EqualsAndHashCode(of = "id")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(nullable = false)
    @Min(value = 1, message = "Le numéro doit être au moins 1")
    @Max(value = 99, message = "Le numéro ne peut pas dépasser 99")
    private Integer number;

    @Column(name = "date_of_birth")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateOfBirth;

    @Column(length = 50)
    private String nationality;

    @Column(unique = true, length = 100)
    @Email(message = "Format email invalide")
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "height_cm")
    @Min(value = 0, message = "La taille doit être positive")
    private Double heightCm;

    @Column(name = "weight_kg")
    @Min(value = 0, message = "Le poids doit être positif")
    private Double weightKg;

    @Column(name = "preferred_foot", length = 10)
    private String preferredFoot; // LEFT, RIGHT, BOTH

    @Column(name = "main_position", nullable = false, length = 20)
    @NotBlank(message = "La position principale est obligatoire")
    private String mainPosition;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "player_secondary_positions", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "position", length = 20)
    private List<String> secondaryPositions = new ArrayList<>();

    @Column(length = 20)
    private String status; // ACTIVE, INJURED, SUSPENDED, etc.

    @Builder.Default
    @Column(name = "matches_played")
    @Min(value = 0)
    private Integer matchesPlayed = 0;

    @Builder.Default
    @Column(name = "total_goals")
    @Min(value = 0)
    private Integer totalGoals = 0;

    @Builder.Default
    @Column(name = "total_assists")
    @Min(value = 0)
    private Integer totalAssists = 0;

    @Column(name = "current_rating")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double currentRating;

    @Column(name = "speed_rating")
    @Min(value = 0)
    @Max(value = 100)
    private Integer speedRating;

    @Column(name = "stamina_rating")
    @Min(value = 0)
    @Max(value = 100)
    private Integer staminaRating;

    @Column(name = "shooting_rating")
    @Min(value = 0)
    @Max(value = 100)
    private Integer shootingRating;

    @Column(name = "passing_rating")
    @Min(value = 0)
    @Max(value = 100)
    private Integer passingRating;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Méthode utilitaire pour calculer l'âge
    public Integer getAge() {
        if (dateOfBirth == null) return null;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    // Méthode pour générer le nom complet automatiquement
    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (fullName == null || fullName.isBlank()) {
            fullName = firstName + " " + lastName;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
}
