package com.coachpad.presentation.rest.dto;

import com.coachpad.domain.model.enums.JerseyDesign;
import com.coachpad.domain.model.enums.WidgetAppearance;
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

    @NotNull(message = "Le style ne peut pas Ãªtre null")
    private WidgetAppearance style;

    private String logoFilePath;

    private String logoIconName;

    private Boolean usePlayerPhotos;

    @NotNull(message = "Le design du maillot ne peut pas Ãªtre null")
    private JerseyDesign jerseyDesign;

    @Valid
    private TeamKitColorsDTO colors;

    private WidgetAppearance gkStyle;
    private JerseyDesign gkJerseyDesign;
    private TeamKitColorsDTO gkColors;

    public TeamDesignDTO(WidgetAppearance style, String logoFilePath, String logoIconName, 
                         JerseyDesign jerseyDesign, TeamKitColorsDTO colors) {
        this.style = style;
        this.logoFilePath = logoFilePath;
        this.logoIconName = logoIconName;
        this.jerseyDesign = jerseyDesign;
        this.colors = colors;
    }

    public boolean hasValidData() {
        return colors != null && 
               jerseyDesign != null && 
               (getHasCustomLogo() || getHasIconLogo());
    }

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
