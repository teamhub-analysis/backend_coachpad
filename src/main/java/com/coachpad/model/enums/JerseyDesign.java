package com.coachpad.model.enums;

import lombok.Getter;

@Getter
public enum JerseyDesign {
    SOLID("Uni", "Maillot couleur unie"),
    STRIPED_VERTICAL("Rayures verticales", "Rayures verticales classiques"),
    STRIPED_HORIZONTAL("Rayures horizontales", "Rayures horizontales"),
    HALF_AND_HALF("Moitié-moitié", "Deux couleurs divisées verticalement"),
    DIAGONAL("Diagonale", "Bande diagonale"),
    CHEVRON("Chevron", "Motif en V"),
    SASH("Écharpe", "Bande en diagonale type écharpe"),
    QUARTERS("Quartiers", "Divisé en 4 quartiers"),
    HOOPS("Cerceaux", "Rayures horizontales larges"),
    PINSTRIPES("Fines rayures", "Rayures verticales fines"),
    GRADIENT("Dégradé", "Dégradé de couleurs"),
    GEOMETRIC("Géométrique", "Motifs géométriques"),
    CUSTOM("Personnalisé", "Design personnalisé"),
    PLAIN("Uni", "Design uni par défaut (alias de SOLID)");

    private final String displayName;
    private final String description;

    JerseyDesign(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Retourne la clé i18n pour ce design
     */
    public String getI18nKey() {
        return "design.jersey." + this.name();
    }

    /**
     * Vérifie si le design nécessite deux couleurs minimum
     */
    public boolean requiresMultipleColors() {
        return this != SOLID && this != CUSTOM;
    }

    /**
     * Retourne tous les designs disponibles sous forme de liste
     */
    public static java.util.List<JerseyDesign> getAllDesigns() {
        return java.util.Arrays.asList(JerseyDesign.values());
    }

    /**
     * Trouve un design par son nom d'affichage
     */
    public static JerseyDesign fromDisplayName(String displayName) {
        for (JerseyDesign design : values()) {
            if (design.displayName.equalsIgnoreCase(displayName)) {
                return design;
            }
        }
        throw new IllegalArgumentException("Aucun design trouvé pour : " + displayName);
    }
}
