package com.coachpad.persistence.entity;

import com.coachpad.persistence.Enum.FootballPosition;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "formations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "teams")
@EqualsAndHashCode(of = "id")
public class FormationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "formation_positions",
        joinColumns = @JoinColumn(name = "formation_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    @OrderColumn(name = "position_order")
    @Builder.Default
    private List<FootballPosition> orderedPositions = new ArrayList<>();

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TeamEntity> teams = new ArrayList<>();

    public int getPositionCount() {
        return orderedPositions.size();
    }

    public boolean isValid() {
        return orderedPositions.size() == 11 &&
               orderedPositions.stream()
                   .filter(FootballPosition::isGoalkeeper)
                   .count() == 1;
    }

    public long getDefenderCount() {
        return orderedPositions.stream()
            .filter(FootballPosition::isDefender)
            .count();
    }

    public long getMidfielderCount() {
        return orderedPositions.stream()
            .filter(FootballPosition::isMidfielder)
            .count();
    }

    public long getForwardCount() {
        return orderedPositions.stream()
            .filter(FootballPosition::isForward)
            .count();
    }

    public String getFormationFormat() {
        if (!isValid()) {
            return "Invalid";
        }
        return String.format("%d-%d-%d",
                getDefenderCount(),
                getMidfielderCount(),
                getForwardCount());
    }
}
