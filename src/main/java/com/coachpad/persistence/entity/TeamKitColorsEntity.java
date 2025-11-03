package com.coachpad.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_colors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "design")
@EqualsAndHashCode(of = "id")
public class TeamKitColorsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primary_hex", nullable = false, length = 7)
    private String primaryHex;

    @Column(name = "secondary_hex", length = 7)
    private String secondaryHex;

    @Column(name = "trim_hex", length = 7)
    private String trimHex;

    @OneToOne(mappedBy = "colors", fetch = FetchType.LAZY)
    private TeamDesignEntity design;

    /**
     * Valide si une couleur hexadécimale est au bon format (#RRGGBB ou #RGB)
     */
    public static boolean isValidHexColor(String hex) {
        if (hex == null) return false;
        return hex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }

    /**
     * Normalise une couleur hex en format #RRGGBB
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
     * Convertit une couleur hex en RGB
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
     * Calcule le contraste entre deux couleurs
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
     * Vérifie si le contraste est suffisant (WCAG AA = 4.5:1)
     */
    public boolean hasGoodContrast() {
        if (primaryHex == null || secondaryHex == null) {
            return true;
        }
        return calculateContrast(primaryHex, secondaryHex) >= 4.5;
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (primaryHex != null) {
            this.primaryHex = normalizeHexColor(primaryHex);
            if (!isValidHexColor(this.primaryHex)) {
                throw new IllegalArgumentException("Invalid primary color: " + primaryHex);
            }
        }
        if (secondaryHex != null) {
            this.secondaryHex = normalizeHexColor(secondaryHex);
            if (!isValidHexColor(this.secondaryHex)) {
                throw new IllegalArgumentException("Invalid secondary color: " + secondaryHex);
            }
        }
        if (trimHex != null) {
            this.trimHex = normalizeHexColor(trimHex);
            if (!isValidHexColor(this.trimHex)) {
                throw new IllegalArgumentException("Invalid trim color: " + trimHex);
            }
        }
    }
}