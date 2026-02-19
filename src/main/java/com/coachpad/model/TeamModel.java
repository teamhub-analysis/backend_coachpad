package com.coachpad.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamModel {

    private Long id;
    private String name;
    private String nickname;
    private FormationModel formation;
    private List<CoachModel> coaches;
    private TeamDesignModel design;
    private List<PlayerModel> players;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
