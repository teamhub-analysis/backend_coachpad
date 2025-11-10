package com.coachpad.model;

import com.coachpad.persistence.Enum.FootballPosition;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FormationModel {

    private Long id;
 
    private List<FootballPosition> orderedPositions;
    private String formationFormat;
    private boolean valid;
}
