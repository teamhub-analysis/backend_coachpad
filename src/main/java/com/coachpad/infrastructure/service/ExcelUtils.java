package com.coachpad.infrastructure.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.coachpad.presentation.rest.dto.PlayerDTO;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExcelUtils {

    public static String normalize(String s) {
        if (s == null)
            return "";
        return java.text.Normalizer.normalize(s.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^a-z0-9 ]", "")
                .trim();
    }

    public static String getCellString(Cell cell) {
        if (cell == null)
            return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue() != null ? cell.getStringCellValue().trim() : "";
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        yield String.valueOf((int) cell.getNumericCellValue());
                    } catch (Exception e2) {
                        yield "";
                    }
                }
            }
            default -> "";
        };
    }

    public static String getString(Row row, int index, String def) {
        if (row == null)
            return def;
        Cell cell = row.getCell(index);
        if (cell == null)
            return def;
        String value = getCellString(cell);
        return (value.isEmpty()) ? def : value;
    }

    public static Integer getInt(Row row, int index) {
        if (row == null)
            return null;
        Cell cell = row.getCell(index);
        if (cell == null)
            return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim().replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static Double getDouble(Row row, int index) {
        if (row == null)
            return null;
        Cell cell = row.getCell(index);
        if (cell == null)
            return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().trim().replace(",", "."));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static boolean isEmpty(Row row) {
        if (row == null)
            return true;
        for (Cell c : row) {
            if (c.getCellType() != CellType.BLANK) {
                String val = getCellString(c);
                if (!val.isBlank())
                    return false;
            }
        }
        return true;
    }

    public static Map<String, Integer> buildColumnMap(Row headerRow) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Cell cell : headerRow) {
            String value = normalize(getCellString(cell));
            if (!value.isEmpty()) {
                map.put(value, cell.getColumnIndex());
            }
        }
        return map;
    }

    public static int findColumn(Map<String, Integer> colMap, List<String> aliases) {
        for (Map.Entry<String, Integer> entry : colMap.entrySet()) {
            for (String alias : aliases) {
                if (entry.getKey().equals(normalize(alias))) {
                    return entry.getValue();
                }
            }
        }
        for (Map.Entry<String, Integer> entry : colMap.entrySet()) {
            for (String alias : aliases) {
                String normAlias = normalize(alias);
                if (entry.getKey().contains(normAlias) || normAlias.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return -1;
    }

    public static String getByHeaders(Row row, Map<String, Integer> colMap,
                                       List<String> aliases, String defaultValue) {
        int col = findColumn(colMap, aliases);
        if (col >= 0) {
            return getString(row, col, defaultValue);
        }
        return defaultValue;
    }

    public static String findValue(Map<String, String> data, List<String> aliases, String defaultValue) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            for (String alias : aliases) {
                if (entry.getKey().equals(normalize(alias)) || entry.getKey().contains(normalize(alias))) {
                    return entry.getValue();
                }
            }
        }
        return defaultValue;
    }

    public static boolean matchesAny(String value, List<String> aliases) {
        for (String alias : aliases) {
            String normAlias = normalize(alias);
            if (value.equals(normAlias) || value.contains(normAlias) || normAlias.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public static String ensureHex(String value, String defaultHex) {
        if (value == null || value.isBlank())
            return defaultHex;
        value = value.trim();
        if (!value.startsWith("#"))
            value = "#" + value;
        if (value.matches("#[0-9A-Fa-f]{3,8}"))
            return value;
        return defaultHex;
    }

    public static String[] smartSplitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"", ""};
        }
        fullName = fullName.trim();
        if (fullName.contains(",")) {
            String[] parts = fullName.split(",", 2);
            return new String[]{parts[1].trim(), parts[0].trim()};
        }
        String[] parts = fullName.split("\\s+", 2);
        if (parts.length == 1) {
            return new String[]{parts[0], parts[0]};
        }
        return new String[]{parts[0], parts[1]};
    }

    public static void validatePlayers(List<PlayerDTO> players) {
        players.removeIf(p -> (p.getFirstName() == null || p.getFirstName().isBlank())
                && (p.getLastName() == null || p.getLastName().isBlank()));
        if (players.isEmpty()) {
            throw new RuntimeException("Aucun joueur valide trouvé dans le fichier");
        }
        for (PlayerDTO p : players) {
            if (p.getFirstName() == null || p.getFirstName().isBlank()) {
                p.setFirstName(p.getLastName());
            }
            if (p.getLastName() == null || p.getLastName().isBlank()) {
                p.setLastName(p.getFirstName());
            }
        }
    }

    public static void assignNumbers(List<PlayerDTO> players, Set<Integer> used) {
        Set<Integer> assigned = new java.util.HashSet<>();
        int next = 1;
        for (PlayerDTO p : players) {
            Integer num = p.getNumber();
            if (num == null || num < 1 || num > 99 || assigned.contains(num)) {
                while ((used.contains(next) || assigned.contains(next)) && next <= 99) {
                    next++;
                }
                if (next > 99)
                    next = 1;
                p.setNumber(next);
                assigned.add(next);
            } else {
                assigned.add(num);
            }
        }
    }
}
