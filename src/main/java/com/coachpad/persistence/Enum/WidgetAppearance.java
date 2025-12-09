package com.coachpad.persistence.Enum;

import lombok.Getter;

@Getter
public enum WidgetAppearance {
    CIRCLE("Cercle", "Apparence en forme de cercle"),
    TRIANGLE("Triangle", "Apparence en forme de triangle"),
    PULL("Pull", "Apparence de type pull"),
    JERSEY("Maillot", "Apparence de type maillot"),
    IMAGE_RECTANGLE("Image Rectangle", "Image dans un cadre rectangulaire"),
    IMAGE_CIRCLE("Image Cercle", "Image dans un cadre circulaire"),
    CUSTOM("Galerie", "Sélection depuis une galerie d'images personnalisée");

    private final String displayName;
    private final String description;

    WidgetAppearance(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Retourne toutes les apparences disponibles sous forme de liste
     */
    public static java.util.List<WidgetAppearance> getAllAppearances() {
        return java.util.Arrays.asList(WidgetAppearance.values());
    }

    /**
     * Trouve une apparence par son nom d'affichage
     */
    public static WidgetAppearance fromDisplayName(String displayName) {
        for (WidgetAppearance appearance : values()) {
            if (appearance.displayName.equalsIgnoreCase(displayName)) {
                return appearance;
            }
        }
        throw new IllegalArgumentException("Aucune apparence trouvée pour : " + displayName);
    }
}