package com.coachpad.domain.model.util;

public class ColorUtils {

    private static final double WCAG_AA_RATIO = 4.5;
    private static final int CHANNEL_COUNT = 3;
    private static final double LUMINANCE_THRESHOLD = 0.03928;
    private static final double LUMINANCE_DIVISOR = 12.92;
    private static final double LUMINANCE_NUMERATOR = 0.055;
    private static final double LUMINANCE_DENOMINATOR = 1.055;
    private static final double LUMINANCE_EXPONENT = 2.4;
    private static final double CHANNEL_WEIGHT_R = 0.2126;
    private static final double CHANNEL_WEIGHT_G = 0.7152;
    private static final double CHANNEL_WEIGHT_B = 0.0722;
    private static final double RGB_MAX = 255.0;

    public static boolean isValidHexColor(String hex) {
        if (hex == null) return false;
        return hex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }

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

    public static double calculateContrast(String hex1, String hex2) {
        int[] rgb1 = hexToRgb(hex1);
        int[] rgb2 = hexToRgb(hex2);
        double l1 = calculateRelativeLuminance(rgb1);
        double l2 = calculateRelativeLuminance(rgb2);
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        return (lighter + 0.05) / (darker + 0.05);
    }

    public static double calculateRelativeLuminance(int[] rgb) {
        double[] channels = new double[CHANNEL_COUNT];
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            double c = rgb[i] / RGB_MAX;
            channels[i] = c <= LUMINANCE_THRESHOLD ? c / LUMINANCE_DIVISOR
                    : Math.pow((c + LUMINANCE_NUMERATOR) / LUMINANCE_DENOMINATOR, LUMINANCE_EXPONENT);
        }
        return CHANNEL_WEIGHT_R * channels[0] + CHANNEL_WEIGHT_G * channels[1] + CHANNEL_WEIGHT_B * channels[2];
    }

    public static boolean hasGoodContrast(String primaryHex, String secondaryHex) {
        if (primaryHex == null || secondaryHex == null) {
            return true;
        }
        return calculateContrast(primaryHex, secondaryHex) >= WCAG_AA_RATIO;
    }
}
