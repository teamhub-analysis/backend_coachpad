package com.coachpad.service;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.dto.TeamDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelImportService {

    public TeamDTO parseTeamExcel(MultipartFile file) throws IOException {
        List<PlayerDTO> players = new ArrayList<>();
        String teamName = "Nouvelle Équipe";
        java.util.Set<Integer> usedNumbers = new java.util.HashSet<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // On peut aussi essayer de deviner le nom de l'équipe à partir du nom du fichier
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && !originalFilename.isEmpty()) {
                teamName = originalFilename.replace(".xlsx", "").replace(".xls", "");
            }

            // On va stocker les joueurs temporairement pour gérer les numéros en deux temps
            List<PlayerDTO> tempPlayers = new ArrayList<>();
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // Sauter l'en-tête

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (isRowEmpty(currentRow)) continue;

                String firstName = getCellValueAsString(currentRow.getCell(0));
                if (firstName == null || firstName.isBlank()) continue;

                String lastName = getCellValueAsString(currentRow.getCell(1));
                if (lastName == null || lastName.isBlank()) lastName = firstName;

                Integer number = getCellValueAsInt(currentRow.getCell(2));
                String mainPosition = getCellValueAsString(currentRow.getCell(3));
                if (mainPosition == null || mainPosition.isBlank()) mainPosition = "TBD";

                PlayerDTO player = PlayerDTO.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .number(number)
                        .mainPosition(mainPosition)
                        .category(getCellValueAsString(currentRow.getCell(4)))
                        .nationality(getCellValueAsString(currentRow.getCell(5)))
                        .build();
                
                tempPlayers.add(player);
                if (number != null && number >= 1 && number <= 99) {
                    usedNumbers.add(number);
                }
            }

            // Deuxième passage : Attribuer des numéros uniques à ceux qui n'en ont pas ou ont des doublons
            java.util.Set<Integer> finalNumbers = new java.util.HashSet<>();
            int nextAvailable = 1;
            for (PlayerDTO p : tempPlayers) {
                Integer num = p.getNumber();
                // Si le numéro est absent, hors limites, ou déjà utilisé par un autre joueur dans ce même import
                if (num == null || num < 1 || num > 99 || finalNumbers.contains(num)) {
                    while ((usedNumbers.contains(nextAvailable) || finalNumbers.contains(nextAvailable)) && nextAvailable < 99) {
                        nextAvailable++;
                    }
                    
                    // Vérification finale pour éviter les doublons si on a atteint 99
                    if (finalNumbers.contains(nextAvailable) || usedNumbers.contains(nextAvailable)) {
                        throw new IllegalStateException("Plus de numéros disponibles pour cette équipe (limite de 1 à 99 atteinte).");
                    }
                    
                    p.setNumber(nextAvailable);
                    finalNumbers.add(nextAvailable);
                } else {
                    finalNumbers.add(num);
                }
                players.add(p);
            }
        }

        return TeamDTO.builder()
                .name(teamName)
                .players(players)
                .build();
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
