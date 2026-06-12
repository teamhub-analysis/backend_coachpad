package com.coachpad.model;

import com.coachpad.model.enums.JerseyDesign;
import com.coachpad.model.enums.WidgetAppearance;

import lombok.*;

/**
 * Modèle métier représentant le design d'une équipe (logo, maillot, couleurs...).
 * Cette classe est utilisée dans la couche service/métier.
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

    /** Chemin du logo personnalisé (si présent) */
    private String logoFilePath;

    /** Nom de l'icône Flutter à utiliser comme logo (si aucun fichier n’est fourni) */
    private String logoIconName;

    /** Si true, l'application utilise les photos réelles des joueurs au lieu des formes tactiques */
    private boolean usePlayerPhotos;

    /** Type de design du maillot (ex: STRIPED, SOLID...) */
    private JerseyDesign jerseyDesign;

    /** Couleurs du maillot et des éléments associés */
    private TeamKitColorsModel colors;

    /**
     * Retourne true si le design a un logo personnalisé (fichier uploadé)
     */
    public boolean hasCustomLogo() {
        return logoFilePath != null && !logoFilePath.trim().isEmpty();
    }

    /**
     * Retourne true si le design utilise une icône Flutter comme logo
     */
    public boolean hasIconLogo() {
        return logoIconName != null && !logoIconName.trim().isEmpty();
    }

    /**
     * Retourne le chemin complet du logo ou null si aucun logo n’existe
     */
    public String getFullLogoPath() {
        if (hasCustomLogo()) {
            return logoFilePath;
        }
        return null;
    }

    /**
     * Valide que le design contient bien toutes les informations nécessaires
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
                hasCustomLogo() ? "Fichier" : (hasIconLogo() ? "Icône" : "Aucun"),
                colors != null ? colors.getPreview() : "N/A"
        );
    }
}
