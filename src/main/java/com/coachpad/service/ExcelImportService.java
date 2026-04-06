package com.coachpad.service;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.dto.TeamDTO;
import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.dto.TeamKitColorsDTO;
import com.coachpad.persistence.Enum.JerseyDesign;
import com.coachpad.persistence.Enum.WidgetAppearance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelImportService {

    @Value("${coachpad.upload.dir}")
    private String uploadDir;

    public TeamDTO parseTeamExcel(MultipartFile file) throws IOException {
        // --- SAUVEGARDE PHYSIQUE DU FICHIER ---
        String savedPath = saveFileToDisk(file);

        List<PlayerDTO> players = new ArrayList<>();
        String teamName = "Nouvelle Équipe";
        java.util.Set<Integer> usedNumbers = new java.util.HashSet<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && !originalFilename.isEmpty()) {
                teamName = originalFilename.replace(".xlsx", "").replace(".xls", "");
            }

            // --- 1. DÉTECTION INTELLIGENTE DE LA LIGNE D'EN-TÊTE (Auto-Scan 5 premières lignes) ---
            int headerRowNum = findHeaderRow(sheet);
            Row headerRow = sheet.getRow(headerRowNum);
            Map<String, Integer> headerMap = detectHeaders(headerRow);

            // On commence à lire les données après l'en-tête
            int startRow = headerRowNum + 1;
            List<PlayerDTO> tempPlayers = new ArrayList<>();
            
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                if (isRowEmpty(currentRow)) continue;

                // --- 2. GESTION INTELLIGENTE DES NOMS (Split si besoin) ---
                String firstName = getMappedValue(currentRow, headerMap, "firstName", 0);
                String lastName = getMappedValue(currentRow, headerMap, "lastName", 1);
                
                // Si on n'a qu'un seul nom, on essaie de le splitter
                if ((firstName == null || firstName.isBlank()) && (lastName != null && !lastName.isBlank())) {
                    String[] parts = lastName.trim().split("\\s+", 2);
                    if (parts.length > 1) {
                        firstName = parts[0];
                        lastName = parts[1];
                    } else {
                        firstName = lastName;
                    }
                } else if ((lastName == null || lastName.isBlank()) && (firstName != null && !firstName.isBlank())) {
                    String[] parts = firstName.trim().split("\\s+", 2);
                    if (parts.length > 1) {
                        firstName = parts[0];
                        lastName = parts[1];
                    } else {
                        lastName = firstName;
                    }
                }

                // Si pas de nom du tout après split, on ignore
                if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) continue;
                
                // Fallback si l'un des deux manque encore
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

        // --- 3. TRAÇABILITÉ (Mapping source et fichier) ---
        TeamDTO dto = createTeamDTO(teamName, players);
        dto.setSource("EXCEL");
        dto.setImportFileName(savedPath);
        return dto;
    }

    private int findHeaderRow(Sheet sheet) {
        int bestRow = 0;
        int maxMatches = -1;
        
        // Scan les 5 premières lignes
        for (int i = 0; i < Math.min(sheet.getLastRowNum(), 10); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            int matches = 0;
            for (int j = 0; j < row.getLastCellNum(); j++) {
                String val = getCellValueAsString(row.getCell(j));
                if (val != null && isMatch(val.toLowerCase(), "nom", "prénom", "first", "last", "poste", "position", "numéro", "number")) {
                    matches++;
                }
            }
            if (matches > maxMatches) {
                maxMatches = matches;
                bestRow = i;
            }
        }
        return bestRow;
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
        if (headerRow == null) return map;

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            String value = getCellValueAsString(headerRow.getCell(i));
            if (value == null) continue;
            value = value.toLowerCase().trim();

            if (isMatch(value, "prénom", "first", "prenom", "joueur", "player", "athlete")) map.putIfAbsent("firstName", i);
            if (isMatch(value, "nom", "last", "family", "surname", "full", "entier")) map.putIfAbsent("lastName", i);
            if (isMatch(value, "numéro", "number", "n°", "#", "bib", "dossard", "jersey")) map.putIfAbsent("number", i);
            if (isMatch(value, "poste", "position", "role", "main", "pos", "placement")) map.putIfAbsent("mainPosition", i);
            if (isMatch(value, "catégorie", "category", "age", "class", "section", "année", "birth")) map.putIfAbsent("category", i);
            if (isMatch(value, "nationalité", "nationality", "pays", "country", "nat")) map.putIfAbsent("nationality", i);
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
        if (row == null) return null;
        Integer idx = map.get(key);
        if (idx == null) return null; // Plus de fallback aveugle pour favoriser le split intelligent
        return getCellValueAsString(row.getCell(idx));
    }

    private Integer getMappedValueAsInt(Row row, Map<String, Integer> map, String key, int fallbackIdx) {
        if (row == null) return null;
        Integer idx = map.get(key);
        if (idx == null) return null;
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
                return Integer.parseInt(cell.getStringCellValue().replaceAll("[^0-9]", ""));
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

    private String saveFileToDisk(MultipartFile file) {
        try {
            Path root = Paths.get(uploadDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String originalFilename = file.getOriginalFilename();
            String fileName = timestamp + "_" + (originalFilename != null ? originalFilename : "import.xlsx");

            Files.copy(file.getInputStream(), root.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Fichier Excel sauvegardé sous : " + root.resolve(fileName));
            return fileName;
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du fichier Excel : " + e.getMessage());
            return null;
        }
    }
}
