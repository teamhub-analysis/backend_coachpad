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

    @Column(nullable = false, length = 100, unique = true)
    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    private String name;

    @Column(length = 50)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private FormationEntity formation;

    @Column(name = "age_category", length = 50)
    private String ageCategory;

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoachEntity> coaches = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "team_medical_staff", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "coach_id"))
    private List<CoachEntity> medicalStaff = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "design_id")
    private TeamDesignEntity design;

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerEntity> players = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SquadGroupEntity> groups = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "source", length = 20)
    private String source; // "MANUAL", "EXCEL"

    @Column(name = "import_file_name")
    private String importFileName;

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

    public void addMedicalStaff(CoachEntity staff) {
        if (staff != null) {
            medicalStaff.add(staff);
            staff.setTeam(this);
        }
    }

    public void removeMedicalStaff(CoachEntity staff) {
        if (staff != null) {
            medicalStaff.remove(staff);
            staff.setTeam(null);
        }
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