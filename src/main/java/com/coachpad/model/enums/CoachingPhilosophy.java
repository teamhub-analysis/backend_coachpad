package com.coachpad.model.enums;

import lombok.Getter;

@Getter
public enum CoachingPhilosophy {
    POSSESSION("Possession", "Contrôle du ballon et jeu de passes"),
    COUNTER_ATTACK("Contre-attaque", "Défense solide et transitions rapides"),
    PRESSING("Pressing", "Pression haute et récupération rapide"),
    TIKI_TAKA("Tiki-Taka", "Passes courtes et mouvement constant"),
    DIRECT("Jeu direct", "Jeu vertical et efficace"),
    DEFENSIVE("Défensif", "Organisation défensive prioritaire"),
    ATTACKING("Offensif", "Jeu offensif et spectaculaire"),
    BALANCED("Équilibré", "Équilibre entre défense et attaque"),
    TOTAL_FOOTBALL("Football total", "Fluidité et polyvalence des positions"),
    YOUTH_DEVELOPMENT("Développement jeunes", "Focus sur la formation des jeunes"),
    TACTICAL_FLEXIBILITY("Flexibilité tactique", "Adaptation selon l'adversaire");

    private final String displayName;
    private final String description;

    CoachingPhilosophy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Retourne la clé i18n pour cette philosophie
     */
    public String getI18nKey() {
        return "coach.philosophy." + this.name();
    }

    /**
     * Vérifie si la philosophie est orientée offensive
     */
    public boolean isOffensive() {
        return this == ATTACKING || this == TIKI_TAKA || this == TOTAL_FOOTBALL;
    }

    /**
     * Vérifie si la philosophie est orientée défensive
     */
    public boolean isDefensive() {
        return this == DEFENSIVE || this == COUNTER_ATTACK;
    }

    /**
     * Vérifie si la philosophie est équilibrée
     */
    public boolean isBalanced() {
        return this == BALANCED || this == TACTICAL_FLEXIBILITY;
    }
}
