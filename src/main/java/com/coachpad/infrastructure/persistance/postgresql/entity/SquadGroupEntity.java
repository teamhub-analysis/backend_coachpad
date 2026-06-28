package com.coachpad.infrastructure.persistance.postgresql.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "squad_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SquadGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Builder.Default
    @Column(name = "is_visible")
    private boolean isVisible = true;

    @Builder.Default
    @Column(name = "is_main_group")
    private boolean isMainGroup = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "squad_group_players",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<PlayerEntity> players = new ArrayList<>();
}
