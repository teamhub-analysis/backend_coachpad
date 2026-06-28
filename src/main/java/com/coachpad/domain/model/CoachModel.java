package com.coachpad.domain.model;

import com.coachpad.domain.model.enums.CoachRole;
import com.coachpad.domain.model.enums.CoachingPhilosophy;
import com.coachpad.domain.model.enums.LicenseLevel;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachModel {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String nationality;
    private String photoUrl;
    private LicenseLevel licenseLevel;
    private LocalDate contractEndDate;
    private CoachingPhilosophy coachingPhilosophy;
    private String coachingPhilosophyDescription;
    private CoachRole role;
    private boolean assigned;
}
