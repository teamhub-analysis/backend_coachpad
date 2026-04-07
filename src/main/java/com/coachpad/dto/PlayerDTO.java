package com.coachpad.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDTO {

    private Long id;

    private Long teamId;

    @NotBlank(message = "Le prÃ©nom est obligatoire")
    @Size(max = 50, message = "Le prÃ©nom ne doit pas dÃ©passer 50 caractÃ¨res")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dÃ©passer 50 caractÃ¨res")
    private String lastName;

    private String fullName;

    @NotNull(message = "Le numÃ©ro est obligatoire")
    @Min(value = 1, message = "Le numÃ©ro doit Ãªtre au moins 1")
    @Max(value = 99, message = "Le numÃ©ro ne peut pas dÃ©passer 99")
    private Integer number;

    @JsonFormat(pattern = "yyyy-MM-dd['T'HH:mm:ss[.SSS]]")
    @Past(message = "La date de naissance doit Ãªtre dans le passÃ©")
    private LocalDate dateOfBirth;

    private Integer age;

    @Size(max = 50, message = "La nationalitÃ© ne doit pas dÃ©passer 50 caractÃ¨res")
    private String nationality;

    private String category;

    @Email(message = "Format email invalide")
    @Size(max = 100, message = "L'email ne doit pas dÃ©passer 100 caractÃ¨res")
    private String email;

    @Size(max = 20, message = "Le numÃ©ro de tÃ©lÃ©phone ne doit pas dÃ©passer 20 caractÃ¨res")
    private String phoneNumber;

    @Size(max = 500, message = "L'URL de la photo ne doit pas dÃ©passer 500 caractÃ¨res")
    private String photoUrl;

    @Min(value = 0, message = "La taille doit Ãªtre positive")
    private Double heightCm;

    @Min(value = 0, message = "Le poids doit Ãªtre positif")
    private Double weightKg;

    private String preferredFoot;

    @NotBlank(message = "La position principale est obligatoire")
    private String mainPosition;

    private List<String> secondaryPositions;

    private String status;

    @Min(value = 0, message = "Le nombre de matchs jouÃ©s doit Ãªtre positif")
    private Integer matchesPlayed;

    @Min(value = 0, message = "Le nombre de buts doit Ãªtre positif")
    private Integer totalGoals;

    @Min(value = 0, message = "Le nombre de passes dÃ©cisives doit Ãªtre positif")
    private Integer totalAssists;

    @DecimalMin(value = "0.0", message = "La note doit Ãªtre au moins 0")
    @DecimalMax(value = "10.0", message = "La note ne peut pas dÃ©passer 10")
    private Double currentRating;

    @Min(value = 0)
    @Max(value = 100)
    private Integer speedRating;

    @Min(value = 0)
    @Max(value = 100)
    private Integer staminaRating;

    @Min(value = 0)
    @Max(value = 100)
    private Integer shootingRating;

    @Min(value = 0)
    @Max(value = 100)
    private Integer passingRating;

    @JsonFormat(pattern = "yyyy-MM-dd['T'HH:mm:ss[.SSS][Z]]")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd['T'HH:mm:ss[.SSS][Z]]")
    private LocalDateTime updatedAt;
}
