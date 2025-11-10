package com.coachpad.dto;

import com.coachpad.persistence.Enum.FootballPosition;
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
