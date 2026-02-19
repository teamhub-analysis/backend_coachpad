package com.coachpad.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "players", "coaches", "design" })
@EqualsAndHashCode(of = "id")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    private String name;

    @Column(length = 50)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private FormationEntity formation;

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoachEntity> coaches = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "design_id")
    private TeamDesignEntity design;

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerEntity> players = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Méthodes helper pour gérer la cohérence bidirectionnelle
    public void addPlayer(PlayerEntity player) {
        if (player != null) {
            players.add(player);
            player.setTeam(this);
        }
    }

    public void removePlayer(PlayerEntity player) {
        if (player != null) {
            players.remove(player);
            player.setTeam(null);
        }
    }

    public void addCoach(CoachEntity coach) {
        if (coach != null) {
            coaches.add(coach);
            coach.setTeam(this);
        }
    }

    public void removeCoach(CoachEntity coach) {
        if (coach != null) {
            coaches.remove(coach);
            coach.setTeam(null);
        }
    }

    public void clearPlayers() {
        players.forEach(player -> player.setTeam(null));
        players.clear();
    }

    // Callbacks JPA pour timestamps automatiques
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}