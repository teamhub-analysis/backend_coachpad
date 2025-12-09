package com.coachpad.dto;

import com.coachpad.persistence.Enum.JerseyDesign;
import com.coachpad.persistence.Enum.WidgetAppearance;
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

    private Long teamId;

    @NotNull(message = "Le style ne peut pas être null")
    private WidgetAppearance style;

    private String logoFilePath;

    private String logoIconName;

    @NotNull(message = "Le design du maillot ne peut pas être null")
    private JerseyDesign jerseyDesign;

    @NotNull(message = "Les couleurs ne peuvent pas être null")
    @Valid
    private TeamKitColorsDTO colors;

    /**
     * Constructeur pour la création (sans ID)
     */
    public TeamDesignDTO(WidgetAppearance style, String logoFilePath, String logoIconName, 
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
               (getHasCustomLogo() || getHasIconLogo());
    }

    // Getters pour les propriétés calculées (pour MapStruct)
    public Boolean getHasCustomLogo() {
        return logoFilePath != null && !logoFilePath.trim().isEmpty();
    }

    public Boolean getHasIconLogo() {
        return logoIconName != null && !logoIconName.trim().isEmpty();
    }

    public String getFullLogoPath() {
        if (logoFilePath != null && !logoFilePath.trim().isEmpty()) {
            return logoFilePath;
        }
        return null;
    }

    public Boolean getIsValid() {
        return hasValidData();
    }
}