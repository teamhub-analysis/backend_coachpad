package com.coachpad.dto;

import com.coachpad.persistence.Enum.CoachingPhilosophy;
import com.coachpad.persistence.Enum.LicenseLevel;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachDTO {
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
    private boolean assigned; // Calculé à partir de isAssigned()
}
