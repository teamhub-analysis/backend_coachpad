package com.coachpad.persistence.entity;

import com.coachpad.persistence.Enum.CoachingPhilosophy;
import com.coachpad.persistence.Enum.LicenseLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "coaches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "team")
@EqualsAndHashCode(of = "id")
public class CoachEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "photo_url")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "license_level", length = 50)
    private LicenseLevel licenseLevel;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "coaching_philosophy", length = 50)
    private CoachingPhilosophy coachingPhilosophy;

    @Column(name = "coaching_philosophy_description", columnDefinition = "TEXT")
    private String coachingPhilosophyDescription;

    @OneToOne(mappedBy = "headCoach", fetch = FetchType.LAZY)
    private TeamEntity team;

    // --- Méthodes utilitaires ---

    public String getDisplayName() {
        if (fullName != null && !fullName.trim().isEmpty()) return fullName.trim();
        String fn = (firstName != null) ? firstName.trim() : "";
        String ln = (lastName != null) ? lastName.trim() : "";
        return (fn + " " + ln).trim();
    }

    public boolean hasPhoto() {
        return photoUrl != null && !photoUrl.trim().isEmpty();
    }

    public boolean isContractValid() {
        if (contractEndDate == null) return true;
        return !contractEndDate.isBefore(LocalDate.now());
    }

    public long getDaysRemainingOnContract() {
        if (contractEndDate == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(LocalDate.now(), contractEndDate);
    }

    public boolean isContractExpiringSoon() {
        long days = getDaysRemainingOnContract();
        return days > 0 && days <= 180;
    }

    public boolean hasProfessionalLicense() {
        return licenseLevel != null && licenseLevel.isProfessional();
    }

    public boolean isAssigned() {
        return team != null;
    }

    public boolean isAvailable() {
        return !isAssigned() && isContractValid();
    }

    public String getExperienceLevel() {
        if (licenseLevel == null) return "Débutant";
        return switch (licenseLevel) {
            case UEFA_PRO -> "Expert";
            case UEFA_A -> "Avancé";
            case UEFA_B -> "Intermédiaire";
            case UEFA_C -> "Confirmé";
            case GRASSROOTS -> "Débutant";
            case NONE -> "Novice";
        };
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (fullName == null || fullName.trim().isEmpty()) {
            String fn = (firstName != null) ? firstName.trim() : "";
            String ln = (lastName != null) ? lastName.trim() : "";
            fullName = (fn + " " + ln).trim();
        }
        if (licenseLevel == null) licenseLevel = LicenseLevel.NONE;
        if (coachingPhilosophy == null) coachingPhilosophy = CoachingPhilosophy.BALANCED;
        if (firstName != null) firstName = firstName.trim();
        if (lastName != null) lastName = lastName.trim();
    }
}
