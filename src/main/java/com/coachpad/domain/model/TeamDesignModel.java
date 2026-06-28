package com.coachpad.domain.model;

import com.coachpad.domain.model.enums.JerseyDesign;
import com.coachpad.domain.model.enums.WidgetAppearance;

import lombok.*;

/**
 * ModÃ¨le mÃ©tier reprÃ©sentant le design d'une Ã©quipe (logo, maillot, couleurs...).
 * Cette classe est utilisÃ©e dans la couche service/mÃ©tier.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class TeamDesignModel {

    private Long id;

    /** Style visuel global (ex: MODERN, CLASSIC...) */
    private WidgetAppearance style;

    /** Chemin du logo personnalisÃ© (si prÃ©sent) */
    private String logoFilePath;

    /** Nom de l'icÃ´ne Flutter Ã  utiliser comme logo (si aucun fichier n'est fourni) */
    private String logoIconName;

    /** Si true, l'application utilise les photos rÃ©elles des joueurs au lieu des formes tactiques */
    private boolean usePlayerPhotos;

    /** Type de design du maillot (ex: STRIPED, SOLID...) */
    private JerseyDesign jerseyDesign;

    /** Couleurs du maillot et des Ã©lÃ©ments associÃ©s */
    private TeamKitColorsModel colors;

    /**
     * Retourne true si le design a un logo personnalisÃ© (fichier uploadÃ©)
     */
    public boolean hasCustomLogo() {
        return logoFilePath != null && !logoFilePath.trim().isEmpty();
    }

    /**
     * Retourne true si le design utilise une icÃ´ne Flutter comme logo
     */
    public boolean hasIconLogo() {
        return logoIconName != null && !logoIconName.trim().isEmpty();
    }

    /**
     * Retourne le chemin complet du logo ou null si aucun logo n'existe
     */
    public String getFullLogoPath() {
        if (hasCustomLogo()) {
            return logoFilePath;
        }
        return null;
    }

    /**
     * Valide que le design contient bien toutes les informations nÃ©cessaires
     */
    public boolean isValid() {
        return colors != null &&
               jerseyDesign != null &&
               (hasCustomLogo() || hasIconLogo());
    }

    /**
     * Retourne une description simple du design (utile pour debug ou affichage)
     */
    public String getDescription() {
        return String.format(
                "Style: %s | Maillot: %s | Logo: %s | Couleurs: %s",
                style != null ? style.name() : "N/A",
                jerseyDesign != null ? jerseyDesign.name() : "N/A",
                hasCustomLogo() ? "Fichier" : (hasIconLogo() ? "IcÃ´ne" : "Aucun"),
                colors != null ? colors.getPreview() : "N/A"
        );
    }
}
