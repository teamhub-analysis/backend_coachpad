package com.coachpad.service;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.dto.TeamDTO;
import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.dto.TeamKitColorsDTO;
import com.coachpad.persistence.Enum.JerseyDesign;
import com.coachpad.persistence.Enum.WidgetAppearance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ExcelImportService {

    public TeamDTO parseTeamExcel(MultipartFile file) throws IOException {
        List<PlayerDTO> players = new ArrayList<>();
        String teamName = "Nouvelle Équipe";
        java.util.Set<Integer> usedNumbers = new java.util.HashSet<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && !originalFilename.isEmpty()) {
                teamName = originalFilename.replace(".xlsx", "").replace(".xls", "");
            }

            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext()) return createTeamDTO(teamName, players);

            // --- DÉTECTION INTELLIGENTE DES EN-TÊTES ---
            Row headerRow = rows.next();
            Map<String, Integer> headerMap = detectHeaders(headerRow);

            List<PlayerDTO> tempPlayers = new ArrayList<>();
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (isRowEmpty(currentRow)) continue;

                String firstName = getMappedValue(currentRow, headerMap, "firstName", 0);
                String lastName = getMappedValue(currentRow, headerMap, "lastName", 1);
                
                // Si pas de nom du tout, on ignore la ligne
                if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) continue;
                
                // Fallback si l'un des deux manque
                if (firstName == null || firstName.isBlank()) firstName = lastName;
                if (lastName == null || lastName.isBlank()) lastName = firstName;

                Integer number = getMappedValueAsInt(currentRow, headerMap, "number", 2);
                String mainPosition = getMappedValue(currentRow, headerMap, "mainPosition", 3);
                if (mainPosition == null || mainPosition.isBlank()) mainPosition = "TBD";

                PlayerDTO player = PlayerDTO.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .number(number)
                        .mainPosition(mainPosition)
                        .category(getMappedValue(currentRow, headerMap, "category", 4))
                        .nationality(getMappedValue(currentRow, headerMap, "nationality", 5))
                        .build();
                
                tempPlayers.add(player);
                if (number != null && number >= 1 && number <= 99) {
                    usedNumbers.add(number);
                }
            }

            // --- ATTRIBUTION DES NUMÉROS UNIQUES ---
            java.util.Set<Integer> finalNumbers = new java.util.HashSet<>();
            int nextAvailable = 1;
            for (PlayerDTO p : tempPlayers) {
                Integer num = p.getNumber();
                if (num == null || num < 1 || num > 99 || finalNumbers.contains(num)) {
                    while ((usedNumbers.contains(nextAvailable) || finalNumbers.contains(nextAvailable)) && nextAvailable < 99) {
                        nextAvailable++;
                    }
                    if (finalNumbers.contains(nextAvailable) || usedNumbers.contains(nextAvailable)) {
                        // On continue simplement sans numéro ou avec le dernier dispo si saturation
                    } else {
                        p.setNumber(nextAvailable);
                        finalNumbers.add(nextAvailable);
                    }
                } else {
                    finalNumbers.add(num);
                }
                players.add(p);
            }
        }

        return createTeamDTO(teamName, players);
    }

    private TeamDTO createTeamDTO(String name, List<PlayerDTO> players) {
        return TeamDTO.builder()
                .name(name)
                .players(players)
                .design(createDefaultDesign())
                .build();
    }

    private TeamDesignDTO createDefaultDesign() {
        return TeamDesignDTO.builder()
                .style(WidgetAppearance.JERSEY)
                .jerseyDesign(JerseyDesign.SOLID)
                .logoIconName("shield")
                .colors(TeamKitColorsDTO.builder()
                        .primaryHex("#FFFFFF")
                        .secondaryHex("#000000")
                        .trimHex("#FF0000")
                        .build())
                .build();
    }

    private Map<String, Integer> detectHeaders(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            String value = getCellValueAsString(headerRow.getCell(i));
            if (value == null) continue;
            value = value.toLowerCase().trim();

            if (isMatch(value, "prénom", "first", "name", "joueur", "player")) map.putIfAbsent("firstName", i);
            if (isMatch(value, "nom", "last", "family", "surname")) map.putIfAbsent("lastName", i);
            if (isMatch(value, "numéro", "number", "n°", "#", "bib")) map.putIfAbsent("number", i);
            if (isMatch(value, "poste", "position", "role", "main")) map.putIfAbsent("mainPosition", i);
            if (isMatch(value, "catégorie", "category", "age", "class")) map.putIfAbsent("category", i);
            if (isMatch(value, "nationalité", "nationality", "pays", "country")) map.putIfAbsent("nationality", i);
        }
        return map;
    }

    private boolean isMatch(String value, String... keywords) {
        for (String kw : keywords) {
            if (value.contains(kw)) return true;
        }
        return false;
    }

    private String getMappedValue(Row row, Map<String, Integer> map, String key, int fallbackIdx) {
        Integer idx = map.getOrDefault(key, fallbackIdx);
        return getCellValueAsString(row.getCell(idx));
    }

    private Integer getMappedValueAsInt(Row row, Map<String, Integer> map, String key, int fallbackIdx) {
        Integer idx = map.getOrDefault(key, fallbackIdx);
        return getCellValueAsInt(row.getCell(idx));
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private Integer getCellValueAsInt(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
