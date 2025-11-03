package com.coachpad.persistence.Enum;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Énumération des positions de football.
 * Les labels traduits sont gérés par le système d'i18n séparé.
 */
@Getter
public enum FootballPosition {
    
    // Gardien
    GK(PositionCategory.GOALKEEPER),
    
    // Défenseurs
    CB(PositionCategory.DEFENDER),
    RB(PositionCategory.DEFENDER),
    LB(PositionCategory.DEFENDER),
    RWB(PositionCategory.DEFENDER),
    LWB(PositionCategory.DEFENDER),
    SW(PositionCategory.DEFENDER),
    
    // Milieux
    CDM(PositionCategory.MIDFIELDER),
    CM(PositionCategory.MIDFIELDER),
    CAM(PositionCategory.MIDFIELDER),
    RM(PositionCategory.MIDFIELDER),
    LM(PositionCategory.MIDFIELDER),
    
    // Attaquants
    RW(PositionCategory.FORWARD),
    LW(PositionCategory.FORWARD),
    ST(PositionCategory.FORWARD),
    CF(PositionCategory.FORWARD),
    SS(PositionCategory.FORWARD);

    private final PositionCategory category;

    FootballPosition(PositionCategory category) {
        this.category = category;
    }

    /**
     * Retourne le nom de l'enum pour la sérialisation JSON.
     * 
     * @return le nom de la position (ex: "GK", "CB")
     */
    @JsonValue
    public String getName() {
        return this.name();
    }

    /**
     * Retourne la clé i18n pour cette position.
     * Format: position.{code}
     * Ex: position.GK, position.CB
     * 
     * @return la clé de traduction
     */
    public String getI18nKey() {
        return "position." + this.name();
    }

    /**
     * Vérifie si la position est un défenseur.
     * 
     * @return true si la position est un défenseur
     */
    public boolean isDefender() {
        return category == PositionCategory.DEFENDER;
    }

    /**
     * Vérifie si la position est un milieu de terrain.
     * 
     * @return true si la position est un milieu
     */
    public boolean isMidfielder() {
        return category == PositionCategory.MIDFIELDER;
    }

    /**
     * Vérifie si la position est un attaquant.
     * 
     * @return true si la position est un attaquant
     */
    public boolean isForward() {
        return category == PositionCategory.FORWARD;
    }

    /**
     * Vérifie si la position est un gardien.
     * 
     * @return true si la position est un gardien
     */
    public boolean isGoalkeeper() {
        return category == PositionCategory.GOALKEEPER;
    }

    /**
     * Vérifie si la position est sur le côté droit du terrain.
     * 
     * @return true si la position est à droite
     */
    public boolean isRightSide() {
        return this == RB || this == RWB || this == RM || this == RW;
    }

    /**
     * Vérifie si la position est sur le côté gauche du terrain.
     * 
     * @return true si la position est à gauche
     */
    public boolean isLeftSide() {
        return this == LB || this == LWB || this == LM || this == LW;
    }

    /**
     * Vérifie si la position est au centre du terrain.
     * 
     * @return true si la position est centrale
     */
    public boolean isCentral() {
        return this == GK || this == CB || this == SW || this == CDM || 
               this == CM || this == CAM || this == ST || this == CF || this == SS;
    }

    /**
     * Trouve une position par son nom (insensible à la casse).
     * 
     * @param name le nom de la position (ex: "GK", "cb")
     * @return Optional contenant la position si trouvée
     */
    public static Optional<FootballPosition> fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return Arrays.stream(values())
            .filter(pos -> pos.name().equalsIgnoreCase(name.trim()))
            .findFirst();
    }

    /**
     * Retourne toutes les positions d'une catégorie donnée.
     * 
     * @param category la catégorie de positions
     * @return tableau des positions de la catégorie
     */
    public static FootballPosition[] getByCategory(PositionCategory category) {
        return Arrays.stream(values())
            .filter(pos -> pos.category == category)
            .toArray(FootballPosition[]::new);
    }

    /**
     * Retourne toutes les positions défensives.
     * 
     * @return tableau des défenseurs
     */
    public static FootballPosition[] getDefenders() {
        return getByCategory(PositionCategory.DEFENDER);
    }

    /**
     * Retourne toutes les positions de milieu.
     * 
     * @return tableau des milieux
     */
    public static FootballPosition[] getMidfielders() {
        return getByCategory(PositionCategory.MIDFIELDER);
    }

    /**
     * Retourne toutes les positions offensives.
     * 
     * @return tableau des attaquants
     */
    public static FootballPosition[] getForwards() {
        return getByCategory(PositionCategory.FORWARD);
    }

    @Override
    public String toString() {
        return this.name();
    }

    /**
     * Catégories de positions pour faciliter le regroupement.
     */
    public enum PositionCategory {
        GOALKEEPER,
        DEFENDER,
        MIDFIELDER,
        FORWARD;

        /**
         * Retourne la clé i18n pour cette catégorie.
         * Format: position.category.{code}
         * Ex: position.category.GOALKEEPER
         * 
         * @return la clé de traduction
         */
        public String getI18nKey() {
            return "position.category." + this.name();
        }
    }
}