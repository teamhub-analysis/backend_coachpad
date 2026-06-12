package com.coachpad.dto;

import com.coachpad.model.enums.FootballPosition;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationDTO {

    private Long id;
   
    private List<FootballPosition> orderedPositions;
    private String formationFormat; 
    private boolean valid;
}
