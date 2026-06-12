package com.coachpad.model.enums;

import lombok.Getter;

@Getter
public enum CoachRole {
    HEAD_COACH("Entraîneur Principal"),
    ASSISTANT_COACH("Entraîneur Adjoint"),
    GOALKEEPER_COACH("Entraîneur des Gardiens"),
    FITNESS_COACH("Préparateur Physique"),
    ANALYST("Analyste Vidéo"),
    DOCTOR("Médecin"),
    PHYSIOTHERAPIST("Kinésithérapeute"),
    MASSEUR("Masseur"),
    NUTRITIONIST("Nutritionniste");

    private final String displayName;

    CoachRole(String displayName) {
        this.displayName = displayName;
    }
}
