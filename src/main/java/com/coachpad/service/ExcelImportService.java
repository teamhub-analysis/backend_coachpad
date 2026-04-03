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

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Sauter l'en-tête si nécessaire
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                
                // Vérifier si la ligne est vide
                if (isRowEmpty(currentRow)) continue;

                PlayerDTO player = PlayerDTO.builder()
                        .firstName(getCellValueAsString(currentRow.getCell(0)))
                        .lastName(getCellValueAsString(currentRow.getCell(1)))
                        .number(getCellValueAsInt(currentRow.getCell(2)))
                        .mainPosition(getCellValueAsString(currentRow.getCell(3)))
                        .category(getCellValueAsString(currentRow.getCell(4)))
                        .nationality(getCellValueAsString(currentRow.getCell(5)))
                        .build();
                
                if (player.getFirstName() != null && !player.getFirstName().isEmpty()) {
                    players.add(player);
                }
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
