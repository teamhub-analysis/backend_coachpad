package com.coachpad.persistence.Enum;

import lombok.Getter;

@Getter
public enum DesignStyle {
    MODERN("Modern"),
    CLASSIC("Classic"),
    RETRO("Retro"),
    MINIMALIST("Minimalist"),
    VINTAGE("Vintage"),
    FUTURISTIC("Futuristic");

    private final String displayName;

    DesignStyle(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retourne la clé i18n pour ce style
     */
    public String getI18nKey() {
        return "design.style." + this.name();
    }
}
