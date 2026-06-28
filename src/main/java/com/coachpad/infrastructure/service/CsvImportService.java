package com.coachpad.infrastructure.service;

import com.coachpad.domain.model.util.PositionUtils;
import com.coachpad.presentation.rest.dto.*;
import com.coachpad.domain.model.enums.JerseyDesign;
import com.coachpad.domain.model.enums.WidgetAppearance;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;

@Slf4j
@Service
public class CsvImportService {

    private static final String DEFAULT_TEAM_NAME = "Imported Team";
    private static final String DEFAULT_AGE_CATEGORY = "Senior";
    private static final String DEFAULT_PRIMARY = "#FFFFFF";
    private static final String DEFAULT_SECONDARY = "#000000";
    private static final String DEFAULT_TRIM = "#FF0000";

    // Same aliases as ExcelImportService for consistency
    private static final List<String> H_FIRST_NAME = List.of("firstname", "first name", "prenom", "prÃƒÂ©nom");
    private static final List<String> H_LAST_NAME = List.of("lastname", "last name", "nom");
    private static final List<String> H_FULL_NAME = List.of("fullname", "full name", "nom complet", "joueur", "player");
    private static final List<String> H_NUMBER = List.of("number", "numero", "numÃƒÂ©ro", "num", "dossard", "#");
    private static final List<String> H_POSITION = List.of("position", "poste", "pos", "role", "rÃƒÂ´le");
    private static final List<String> H_NATIONALITY = List.of("nationality", "nationalitÃƒÂ©", "pays", "nation");

    public TeamDTO importFullTeam(MultipartFile file) {
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream()))
                .withSkipLines(0) // Read headers
                .build()) {

            List<String[]> rows = reader.readAll();
            if (rows.isEmpty()) {
                throw new RuntimeException("Le fichier CSV est vide");
            }

            String[] headers = rows.get(0);
            Map<String, Integer> colMap = buildColumnMap(headers);

            List<PlayerDTO> players = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length == 0 || (row.length == 1 && row[0].isBlank())) continue;

                PlayerDTO player = parsePlayer(row, colMap);
                if (player != null) {
                    players.add(player);
                }
            }

            String teamName = file.getOriginalFilename() != null 
                ? file.getOriginalFilename().replaceAll("\\.csv$", "").replace("_", " ") 
                : DEFAULT_TEAM_NAME;

            return TeamDTO.builder()
                    .name(teamName)
                    .ageCategory(DEFAULT_AGE_CATEGORY)
                    .players(players)
                    .coaches(new ArrayList<>())
                    .medicalStaff(new ArrayList<>())
                    .source("CSV")
                    .importFileName(file.getOriginalFilename())
                    .design(TeamDesignDTO.builder()
                            .style(WidgetAppearance.JERSEY)
                            .jerseyDesign(JerseyDesign.SOLID)
                            .logoIconName("shield")
                            .colors(TeamKitColorsDTO.builder()
                                    .primaryHex(DEFAULT_PRIMARY)
                                    .secondaryHex(DEFAULT_SECONDARY)
                                    .trimHex(DEFAULT_TRIM)
                                    .build())
                            .build())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Erreur import CSV : " + e.getMessage(), e);
        }
    }

    private Map<String, Integer> buildColumnMap(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].toLowerCase().trim();
            map.put(header, i);
        }
        return map;
    }

    private PlayerDTO parsePlayer(String[] row, Map<String, Integer> colMap) {
        String firstName = getValue(row, colMap, H_FIRST_NAME);
        String lastName = getValue(row, colMap, H_LAST_NAME);
        String fullName = getValue(row, colMap, H_FULL_NAME);
        String numberStr = getValue(row, colMap, H_NUMBER);
        String position = getValue(row, colMap, H_POSITION);
        String nationality = getValue(row, colMap, H_NATIONALITY);

        if (fullName != null && !fullName.isBlank() && (firstName == null || firstName.isBlank())) {
            String[] parts = fullName.split("\\s+", 2);
            firstName = parts[0];
            lastName = parts.length > 1 ? parts[1] : "";
        }

        if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
            return null;
        }

        Integer number = null;
        if (numberStr != null && !numberStr.isBlank()) {
            try {
                number = Integer.parseInt(numberStr.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                log.warn("Invalid number format for '{}', ignoring: {}", numberStr, e.getMessage());
            }
        }

        return PlayerDTO.builder()
                .firstName(firstName != null ? firstName.trim() : "")
                .lastName(lastName != null ? lastName.trim() : "")
                .number(number)
                .mainPosition(PositionUtils.normalize(position))
                .nationality(nationality)
                .build();
    }

    private String getValue(String[] row, Map<String, Integer> colMap, List<String> aliases) {
        for (String alias : aliases) {
            Integer idx = colMap.get(alias);
            if (idx != null && idx < row.length) {
                return row[idx];
            }
        }
        return null;
    }
}
