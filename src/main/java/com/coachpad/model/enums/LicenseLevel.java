package com.coachpad.model.enums;

import lombok.Getter;

@Getter
public enum LicenseLevel {
    UEFA_PRO("UEFA Pro License", "Niveau professionnel le plus élevé", 5),
    UEFA_A("UEFA A License", "Niveau avancé pour clubs professionnels", 4),
    UEFA_B("UEFA B License", "Niveau intermédiaire", 3),
    UEFA_C("UEFA C License", "Niveau de base", 2),
    GRASSROOTS("Grassroots", "Niveau débutant/amateur", 1),
    NONE("Sans licence", "Aucune licence officielle", 0);

    private final String displayName;
    private final String description;
    private final int level;

    LicenseLevel(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }

    /**
     * Retourne la clé i18n pour cette licence
     */
    public String getI18nKey() {
        return "coach.license." + this.name();
    }

    /**
     * Vérifie si la licence est de niveau professionnel
     */
    public boolean isProfessional() {
        return level >= 4;
    }

    /**
     * Vérifie si cette licence est supérieure ou égale à une autre
     */
    public boolean isAtLeast(LicenseLevel other) {
        return this.level >= other.level;
    }

    /**
     * Retourne la prochaine licence à obtenir
     */
    public LicenseLevel getNextLevel() {
        for (LicenseLevel license : values()) {
            if (license.level == this.level + 1) {
                return license;
            }
        }
        return null;
    }
}
