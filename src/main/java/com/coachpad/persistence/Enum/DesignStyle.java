package com.coachpad.persistence.Enum;

import lombok.Getter;

@Getter
public enum DesignStyle {
    MODERN("Modern", "Design moderne et épuré"),
    CLASSIC("Classic", "Style classique intemporel"),
    RETRO("Retro", "Inspiré des années passées"),
    MINIMALIST("Minimalist", "Minimaliste et simple"),
    VINTAGE("Vintage", "Style vintage authentique"),
    FUTURISTIC("Futuristic", "Design futuriste et innovant");

    private final String displayName;
    private final String description;

    DesignStyle(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }


    /**
     * Retourne tous les styles disponibles sous forme de liste
     */
    public static java.util.List<DesignStyle> getAllStyles() {
        return java.util.Arrays.asList(DesignStyle.values());
    }

    /**
     * Trouve un style par son nom d'affichage
     */
    public static DesignStyle fromDisplayName(String displayName) {
        for (DesignStyle style : values()) {
            if (style.displayName.equalsIgnoreCase(displayName)) {
                return style;
            }
        }
        throw new IllegalArgumentException("Aucun style trouvé pour : " + displayName);
    }
}