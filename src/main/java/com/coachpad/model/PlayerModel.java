package com.coachpad.model;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerModel {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private Integer number;
    private LocalDate dateOfBirth;
    private String nationality;
  
    private String photoUrl;
    private Double heightCm;
    private Double weightKg;
    private String preferredFoot;
    private String mainPosition;
    private List<String> secondaryPositions;
    private String status;
    private Integer matchesPlayed;
    private Integer totalGoals;
    private Integer totalAssists;
    private Double currentRating;
    private Integer speedRating;
    private Integer staminaRating;
    private Integer shootingRating;
    private Integer passingRating;
    private TeamModel team; // Relation complète pour la logique métier
}
