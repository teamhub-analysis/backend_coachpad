package com.coachpad.domain.model;

import com.coachpad.domain.model.enums.JerseyDesign;
import com.coachpad.domain.model.enums.WidgetAppearance;

import lombok.*;

import java.util.Optional;

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

    /** Nom de l'icône Flutter à utiliser comme logo (si aucun fichier n'est fourni) */
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
     * Retourne true si un logo est défini, peu importe sa source (fichier ou icône)
     */
    public boolean hasAnyLogo() {
        return hasCustomLogo() || hasIconLogo();
    }

    /**
     * Retourne le chemin du fichier logo, si défini.
     * Vide si le design utilise une icône ou aucun logo.
     */
    public Optional<String> getCustomLogoPath() {
        return hasCustomLogo() ? Optional.of(logoFilePath) : Optional.empty();
    }

    /**
     * Retourne le nom de l'icône logo, si défini.
     * Vide si le design utilise un fichier ou aucun logo.
     */
    public Optional<String> getIconLogoName() {
        return hasIconLogo() ? Optional.of(logoIconName) : Optional.empty();
    }

    /**
     * Valide que le design contient bien toutes les informations nécessaires
     */
    public boolean isValid() {
        return colors != null &&
               jerseyDesign != null &&
               hasAnyLogo();
    }

    /**
     * Retourne une description simple du design (utile pour debug ou affichage)
     */
    public String getDescription() {
        String logoDescription = hasCustomLogo()
                ? "Fichier"
                : hasIconLogo() ? "Icône" : "Aucun";

        return String.format(
                "Style: %s | Maillot: %s | Logo: %s | Couleurs: %s",
                style != null ? style.name() : "N/A",
                jerseyDesign != null ? jerseyDesign.name() : "N/A",
                logoDescription,
                colors != null ? colors.getPreview() : "N/A"
        );
    }
}