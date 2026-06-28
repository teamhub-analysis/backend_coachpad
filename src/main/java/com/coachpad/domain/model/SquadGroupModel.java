package com.coachpad.domain.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SquadGroupModel {
    private Long id;
    private String name;
    private Long teamId;
    private String colorHex;
    private List<Long> playerIds;
    private boolean isVisible;
    private boolean isMainGroup;
}
