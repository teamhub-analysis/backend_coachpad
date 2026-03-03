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

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String lastName;

    private String fullName;

    @NotNull(message = "Le numéro est obligatoire")
    @Min(value = 1, message = "Le numéro doit être au moins 1")
    @Max(value = 99, message = "Le numéro ne peut pas dépasser 99")
    private Integer number;

    @JsonFormat(pattern = "yyyy-MM-dd['T'HH:mm:ss[.SSS]]")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateOfBirth;

    private Integer age;

    @Size(max = 50, message = "La nationalité ne doit pas dépasser 50 caractères")
    private String nationality;

    @Email(message = "Format email invalide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String phoneNumber;

    @Size(max = 500, message = "L'URL de la photo ne doit pas dépasser 500 caractères")
    private String photoUrl;

    @Min(value = 0, message = "La taille doit être positive")
    private Double heightCm;

    @Min(value = 0, message = "Le poids doit être positif")
    private Double weightKg;

    private String preferredFoot;

    @NotBlank(message = "La position principale est obligatoire")
    private String mainPosition;

    private List<String> secondaryPositions;

    private String status;

    @Min(value = 0, message = "Le nombre de matchs joués doit être positif")
    private Integer matchesPlayed;

    @Min(value = 0, message = "Le nombre de buts doit être positif")
    private Integer totalGoals;

    @Min(value = 0, message = "Le nombre de passes décisives doit être positif")
    private Integer totalAssists;

    @DecimalMin(value = "0.0", message = "La note doit être au moins 0")
    @DecimalMax(value = "10.0", message = "La note ne peut pas dépasser 10")
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