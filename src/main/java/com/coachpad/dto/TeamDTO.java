package com.coachpad.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDTO {

    private Long id;

    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String name;

    @Size(max = 50, message = "Le surnom ne doit pas dépasser 50 caractères")
    private String nickname;

    private Long formationId;
    private String formationName;

    private Long headCoachId;
    private String headCoachName;

    private Long designId;
    private TeamDesignDTO design;

    private List<Long> playerIds;      // Pour lier des joueurs existants
    private List<PlayerDTO> players;   // ← AJOUTER CETTE LIGNE pour créer de nouveaux joueurs
    private Integer playerCount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}