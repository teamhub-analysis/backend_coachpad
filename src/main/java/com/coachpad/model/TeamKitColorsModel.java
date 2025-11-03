package com.coachpad.model;

import lombok.*;

/**
 * Modèle métier représentant les couleurs d'un kit d'équipe (maillot, short, etc.).
 * Sert de couche métier entre l'entité JPA et les DTO exposés à l'API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class TeamKitColorsModel {

    private Long id;

    /** Couleur principale du kit (ex: #FF0000) */
    private String primaryHex;

    /** Couleur secondaire du kit (ex: #FFFFFF) */
    private String secondaryHex;

    /** Couleur des bordures / détails (ex: #000000) */
    private String trimHex;

    /**
     * Vérifie si une couleur hexadécimale est valide (#RRGGBB ou #RGB)
     */
    public static boolean isValidHexColor(String hex) {
        if (hex == null) return false;
        return hex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }

    /**
     * Normalise une couleur hexadécimale en format #RRGGBB
     */
    public static String normalizeHexColor(String hex) {
        if (hex == null) return null;

        hex = hex.trim().toUpperCase();

        if (!hex.startsWith("#")) {
            hex = "#" + hex;
        }

        if (hex.length() == 4) {
            String r = hex.substring(1, 2);
            String g = hex.substring(2, 3);
            String b = hex.substring(3, 4);
            hex = "#" + r + r + g + g + b + b;
        }

        return hex;
    }

    /**
     * Convertit une couleur hex en tableau RGB [r, g, b]
     */
    public static int[] hexToRgb(String hex) {
        if (!isValidHexColor(hex)) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }

        String normalized = normalizeHexColor(hex);
        int r = Integer.parseInt(normalized.substring(1, 3), 16);
        int g = Integer.parseInt(normalized.substring(3, 5), 16);
        int b = Integer.parseInt(normalized.substring(5, 7), 16);

        return new int[]{r, g, b};
    }

    /**
     * Calcule le contraste entre deux couleurs HEX selon WCAG 2.1
     */
    public static double calculateContrast(String hex1, String hex2) {
        int[] rgb1 = hexToRgb(hex1);
        int[] rgb2 = hexToRgb(hex2);

        double l1 = calculateRelativeLuminance(rgb1);
        double l2 = calculateRelativeLuminance(rgb2);

        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);

        return (lighter + 0.05) / (darker + 0.05);
    }

    private static double calculateRelativeLuminance(int[] rgb) {
        double[] channels = new double[3];
        for (int i = 0; i < 3; i++) {
            double c = rgb[i] / 255.0;
            channels[i] = c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
        }
        return 0.2126 * channels[0] + 0.7152 * channels[1] + 0.0722 * channels[2];
    }

    /**
     * Vérifie si le contraste entre primaryHex et secondaryHex est suffisant
     * selon la norme WCAG AA (4.5:1)
     */
    public boolean hasGoodContrast() {
        if (primaryHex == null || secondaryHex == null) {
            return true;
        }
        return calculateContrast(primaryHex, secondaryHex) >= 4.5;
    }

    /**
     * Retourne une prévisualisation simple des couleurs du kit
     */
    public String getPreview() {
        return String.format(
                "Primary: %s | Secondary: %s | Trim: %s | Contrast OK: %s",
                primaryHex,
                secondaryHex,
                trimHex,
                hasGoodContrast() ? "✅" : "⚠️"
        );
    }
}
