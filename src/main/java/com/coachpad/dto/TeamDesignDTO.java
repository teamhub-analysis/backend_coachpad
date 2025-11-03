package com.coachpad.dto;

import com.coachpad.persistence.Enum.DesignStyle;
import com.coachpad.persistence.Enum.JerseyDesign;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamDesignDTO {

    private Long id;

    @NotNull(message = "Le style ne peut pas être null")
    private DesignStyle style;

    private String logoFilePath;

    private String logoIconName;

    @NotNull(message = "Le design du maillot ne peut pas être null")
    private JerseyDesign jerseyDesign;

    @NotNull(message = "Les couleurs ne peuvent pas être null")
    @Valid
    private TeamKitColorsDTO colors;

    // Champs calculés
    private Boolean hasCustomLogo;
    private Boolean hasIconLogo;
    private String fullLogoPath;
    private Boolean isValid;

    /**
     * Constructeur pour la création (sans ID)
     */
    public TeamDesignDTO(DesignStyle style, String logoFilePath, String logoIconName, 
                         JerseyDesign jerseyDesign, TeamKitColorsDTO colors) {
        this.style = style;
        this.logoFilePath = logoFilePath;
        this.logoIconName = logoIconName;
        this.jerseyDesign = jerseyDesign;
        this.colors = colors;
    }

    /**
     * Valide les données du DTO
     */
    public boolean hasValidData() {
        return colors != null && 
               jerseyDesign != null && 
               (hasCustomLogo() || hasIconLogo());
    }

    private boolean hasCustomLogo() {
        return logoFilePath != null && !logoFilePath.trim().isEmpty();
    }

    private boolean hasIconLogo() {
        return logoIconName != null && !logoIconName.trim().isEmpty();
    }
}