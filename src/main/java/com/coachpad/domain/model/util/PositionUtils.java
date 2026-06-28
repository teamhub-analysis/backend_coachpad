package com.coachpad.domain.model.util;

public class PositionUtils {

    private static final String TBD = "TBD";

    public static String normalize(String pos) {
        if (pos == null || pos.isBlank())
            return TBD;
        String p = pos.trim().toUpperCase();

        if (p.matches(".*\\b(GK|GB|GOAL|GARDIEN|KEEPER|GOALKEEPER|PORTERO|PORTIERE)\\b.*"))
            return "GK";
        if (p.matches(".*\\b(DEF|DC|DG|DD|CB|LB|RB|BACK|DEFENDER|DEFENSEUR|ARRIERE|ARRIERE|LATERAL|LATERAL)\\b.*"))
            return mapDefenderPosition(p);
        if (p.matches(".*\\b(MID|MC|MG|MD|MDC|MOC|CM|CDM|CAM|LM|RM|MILIEU|MIDFIELDER|MEDIO)\\b.*"))
            return mapMidfielderPosition(p);
        if (p.matches(".*\\b(FW|ATT|ST|CF|LW|RW|FORWARD|ATTAQUANT|STRIKER|AILIER|AVANT|DELANTERO|PUNTA)\\b.*"))
            return mapForwardPosition(p);

        return pos.trim();
    }

    private static String mapDefenderPosition(String p) {
        if (p.contains("DG") || p.contains("LB") || p.contains("GAUCHE") || p.contains("LEFT"))
            return "LB";
        if (p.contains("DD") || p.contains("RB") || p.contains("DROIT") || p.contains("RIGHT"))
            return "RB";
        if (p.contains("DC") || p.contains("CB") || p.contains("CENTRAL"))
            return "CB";
        return "CB";
    }

    private static String mapMidfielderPosition(String p) {
        if (p.contains("MDC") || p.contains("CDM") || p.contains("DEFENSIF") || p.contains("DEFENSIF"))
            return "CDM";
        if (p.contains("MOC") || p.contains("CAM") || p.contains("OFFENSIF"))
            return "CAM";
        if (p.contains("MG") || p.contains("LM") || p.contains("GAUCHE"))
            return "LM";
        if (p.contains("MD") || p.contains("RM") || p.contains("DROIT"))
            return "RM";
        return "CM";
    }

    private static String mapForwardPosition(String p) {
        if (p.contains("LW") || p.contains("AG") || p.contains("AILIER") && p.contains("GAUCHE"))
            return "LW";
        if (p.contains("RW") || p.contains("AD") || p.contains("AILIER") && p.contains("DROIT"))
            return "RW";
        if (p.contains("ST") || p.contains("CF") || p.contains("AVANT") || p.contains("POINTE"))
            return "ST";
        return "ST";
    }
}
