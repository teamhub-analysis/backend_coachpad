package com.coachpad.infrastructure.service.dataimport;

import com.coachpad.domain.model.util.PositionUtils;
import com.coachpad.presentation.rest.dto.*;
import com.coachpad.domain.model.enums.JerseyDesign;
import com.coachpad.domain.model.enums.WidgetAppearance;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

import static com.coachpad.infrastructure.service.dataimport.ExcelUtils.*;

@Slf4j
@Service
public class ExcelImportService {

    public static final String DEFAULT_AGE_CATEGORY = "Senior";
    public static final String DEFAULT_TEAM_NAME = "Imported Team";
    public static final String DEFAULT_POSITION = "TBD";
    public static final String DEFAULT_PRIMARY = "#FFFFFF";
    public static final String DEFAULT_SECONDARY = "#000000";
    public static final String DEFAULT_TRIM = "#FF0000";
    public static final String SOURCE_EXCEL = "EXCEL";

    // =====================================================
    // Aliases for intelligent sheet detection
    // =====================================================

    private static final List<String> TEAM_ALIASES = List.of(
            "team", "equipe", "ÃƒÂ©quipe", "club", "info", "infos", "informations",
            "team info", "team_info", "fiche", "general", "gÃƒÂ©nÃƒÂ©ral", "generale",
            "presentation", "prÃƒÂ©sentation", "donnÃƒÂ©es", "donnees", "data");

    private static final List<String> PLAYERS_ALIASES = List.of(
            "players", "joueurs", "effectif", "roster", "squad", "player",
            "joueur", "liste joueurs", "liste_joueurs", "list", "liste",
            "footballers", "footballeurs", "equipe joueurs", "player list");

    private static final List<String> TECH_STAFF_ALIASES = List.of(
            "technical staff", "staff technique", "staff tech", "staff_technique",
            "coaching staff", "coaches", "coachs", "entraineurs", "entraÃƒÂ®neurs",
            "trainers", "technical", "technique", "encadrement", "encadrement technique",
            "staff", "coaching", "coach");

    private static final List<String> MEDICAL_STAFF_ALIASES = List.of(
            "medical staff", "staff medical", "staff mÃƒÂ©dical", "staff_medical",
            "medical", "mÃƒÂ©dical", "medecins", "mÃƒÂ©decins", "doctors", "medical team",
            "equipe medicale", "ÃƒÂ©quipe mÃƒÂ©dicale", "soins", "sante", "santÃƒÂ©",
            "paramedical", "paramÃƒÂ©dical", "kine", "kinÃƒÂ©");

    // =====================================================
    // Aliases for intelligent column/header detection
    // =====================================================

    // -- Team headers --
    private static final List<String> H_TEAM_NAME = List.of(
            "name", "nom", "team name", "nom equipe", "nom ÃƒÂ©quipe", "club",
            "team", "equipe", "ÃƒÂ©quipe", "nom du club", "nom_equipe");

    private static final List<String> H_TEAM_CATEGORY = List.of(
            "category", "catÃƒÂ©gorie", "categorie", "age category", "age", "cat",
            "categorie age", "catÃƒÂ©gorie d'ÃƒÂ¢ge", "tranche", "niveau");

    private static final List<String> H_TEAM_LOGO = List.of(
            "logo", "logo url", "logo_url", "logo path", "image", "emblem",
            "ecusson", "ÃƒÂ©cusson", "blason", "badge", "crest", "icon", "icone", "icÃƒÂ´ne");

    private static final List<String> H_TEAM_PRIMARY = List.of(
            "primary", "primary color", "couleur primaire", "couleur principale",
            "couleur 1", "color1", "color 1", "primary hex", "primaryhex",
            "couleur_primaire", "main color");

    private static final List<String> H_TEAM_SECONDARY = List.of(
            "secondary", "secondary color", "couleur secondaire",
            "couleur 2", "color2", "color 2", "secondary hex", "secondaryhex",
            "couleur_secondaire");

    private static final List<String> H_TEAM_TRIM = List.of(
            "trim", "trim color", "couleur trim", "couleur bordure",
            "couleur 3", "color3", "color 3", "trim hex", "trimhex",
            "couleur_trim", "accent", "accent color");

    // -- Player headers --
    private static final List<String> H_FIRST_NAME = List.of(
            "firstname", "first name", "first_name", "prenom", "prÃƒÂ©nom",
            "nom de bapteme", "given name", "fname");

    private static final List<String> H_LAST_NAME = List.of(
            "lastname", "last name", "last_name", "nom", "nom de famille",
            "family name", "surname", "lname", "name");

    private static final List<String> H_FULL_NAME = List.of(
            "fullname", "full name", "full_name", "nom complet", "nom_complet",
            "joueur", "player", "nom et prenom", "nom et prÃƒÂ©nom",
            "nom & prenom", "nom & prÃƒÂ©nom");

    private static final List<String> H_NUMBER = List.of(
            "number", "numero", "numÃƒÂ©ro", "num", "nÃ‚Â°", "no", "n",
            "shirt", "shirt number", "maillot", "jersey", "jersey number",
            "dorsal", "dossard", "#");

    private static final List<String> H_POSITION = List.of(
            "position", "poste", "pos", "role", "rÃƒÂ´le",
            "main position", "position principale", "poste principal");

    private static final List<String> H_NATIONALITY = List.of(
            "nationality", "nationalitÃƒÂ©", "nationalite", "nat", "country",
            "pays", "nation", "origin", "origine");

    private static final List<String> H_HEIGHT = List.of(
            "height", "taille", "hauteur", "height cm", "heightcm",
            "taille cm", "taille_cm", "cm");

    private static final List<String> H_WEIGHT = List.of(
            "weight", "poids", "weight kg", "weightkg",
            "poids kg", "poids_kg", "kg", "masse");

    private static final List<String> H_FOOT = List.of(
            "foot", "pied", "preferred foot", "pied prÃƒÂ©fÃƒÂ©rÃƒÂ©", "pied prefere",
            "pied fort", "strong foot", "dominant foot");

    private static final List<String> H_CATEGORY = List.of(
            "category", "catÃƒÂ©gorie", "categorie", "cat",
            "player category", "catÃƒÂ©gorie joueur");

    private static final List<String> H_EMAIL = List.of(
            "email", "e-mail", "mail", "courriel", "adresse email",
            "adresse mail", "email address");

    private static final List<String> H_PHONE = List.of(
            "phone", "telephone", "tÃƒÂ©lÃƒÂ©phone", "tel", "tÃƒÂ©l",
            "phone number", "mobile", "gsm", "portable", "numÃƒÂ©ro tel");

    // -- Staff headers --
    private static final List<String> H_STAFF_ROLE = List.of(
            "role", "rÃƒÂ´le", "fonction", "function", "poste", "position",
            "titre", "title", "job", "spÃƒÂ©cialitÃƒÂ©", "specialite", "speciality");

    // =====================================================
    // MAIN ENTRY POINT
    // =====================================================

    public TeamDTO importFullTeam(MultipartFile file) {
        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            int sheetCount = workbook.getNumberOfSheets();
            log.info("Excel contient {} feuille(s):", sheetCount);
            for (int i = 0; i < sheetCount; i++) {
                log.info("   [{}] {}", i, workbook.getSheetName(i));
            }

            // --- INTELLIGENT SHEET DETECTION ---
            Sheet teamSheet = findSheet(workbook, TEAM_ALIASES);
            Sheet playersSheet = findSheet(workbook, PLAYERS_ALIASES);
            Sheet staffSheet = findSheet(workbook, TECH_STAFF_ALIASES);
            Sheet medicalSheet = findSheet(workbook, MEDICAL_STAFF_ALIASES);

            log.info("Détection des feuilles:");
            log.info("   Team:     {}", teamSheet != null ? teamSheet.getSheetName() : "non trouvée");
            log.info("   Players:  {}", playersSheet != null ? playersSheet.getSheetName() : "non trouvée");
            log.info("   Staff:    {}", staffSheet != null ? staffSheet.getSheetName() : "non trouvée");
            log.info("   Medical:  {}", medicalSheet != null ? medicalSheet.getSheetName() : "non trouvée");

            // --- FALLBACK: If only 1 sheet, treat it as players ---
            if (playersSheet == null && sheetCount == 1) {
                playersSheet = workbook.getSheetAt(0);
                log.info("Fichier avec une seule feuille -> utilisée comme joueurs");
            }

            // --- FALLBACK: If no player sheet found, try unmatched sheets ---
            if (playersSheet == null) {
                for (int i = 0; i < sheetCount; i++) {
                    Sheet s = workbook.getSheetAt(i);
                    if (s != teamSheet && s != staffSheet && s != medicalSheet) {
                        // Check if this sheet looks like a players sheet (has number/position columns)
                        if (looksLikePlayersSheet(s)) {
                            playersSheet = s;
                            log.info("Feuille '{}' détectée comme joueurs par contenu", s.getSheetName());
                            break;
                        }
                    }
                }
            }

            // --- PARSE ---
            TeamDTO team;
            if (teamSheet != null) {
                team = parseTeamIntelligent(teamSheet);
            } else {
                // Build team from filename
                String fileName = file.getOriginalFilename();
                String teamName = fileName != null
                        ? fileName.replaceAll("\\.(xlsx|xls)$", "").replace("_", " ")
                        : DEFAULT_TEAM_NAME;
                team = TeamDTO.builder()
                        .name(teamName)
                        .ageCategory(DEFAULT_AGE_CATEGORY)
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
                log.info("Pas de feuille Team -> nom déduit du fichier: {}", teamName);
            }

            List<PlayerDTO> players = playersSheet != null
                    ? parsePlayersIntelligent(playersSheet)
                    : new ArrayList<>();

            List<CoachDTO> coaches = staffSheet != null
                    ? parseStaffIntelligent(staffSheet)
                    : new ArrayList<>();

            List<CoachDTO> medical = medicalSheet != null
                    ? parseStaffIntelligent(medicalSheet)
                    : new ArrayList<>();

            // --- VALIDATION ---
            if (players.isEmpty() && coaches.isEmpty() && medical.isEmpty()) {
                throw new RuntimeException("Aucune donnÃƒÂ©e trouvÃƒÂ©e dans le fichier Excel");
            }

            // Validate players (allow empty if we have staff)
            if (!players.isEmpty()) {
                validatePlayers(players);
            }

            // --- BUILD FINAL ---
            team.setPlayers(players);
            team.setCoaches(coaches);
            team.setMedicalStaff(medical);
            team.setSource(SOURCE_EXCEL);
            team.setImportFileName(file.getOriginalFilename());

            log.info("Import terminé: {} joueurs, {} staff technique, {} staff médical",
                    players.size(), coaches.size(), medical.size());

            return team;

        } catch (Exception e) {
            throw new RuntimeException("Erreur import Excel : " + e.getMessage(), e);
        }
    }

    // =====================================================
    // INTELLIGENT SHEET DETECTION
    // =====================================================

    private Sheet findSheet(Workbook wb, List<String> aliases) {
        // 1. Exact match
        for (String alias : aliases) {
            Sheet s = wb.getSheet(alias);
            if (s != null)
                return s;
        }

        // 2. Case-insensitive match
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            String sheetName = wb.getSheetName(i).trim().toLowerCase();
            for (String alias : aliases) {
                if (sheetName.equals(alias.toLowerCase())) {
                    return wb.getSheetAt(i);
                }
            }
        }

        // 3. Contains match (the sheet name contains an alias or vice-versa)
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            String sheetName = normalize(wb.getSheetName(i));
            for (String alias : aliases) {
                String normalizedAlias = normalize(alias);
                if (sheetName.contains(normalizedAlias) || normalizedAlias.contains(sheetName)) {
                    return wb.getSheetAt(i);
                }
            }
        }

        return null; // Not found - that's OK, we handle it gracefully
    }

    /** Check if an unmatched sheet looks like a players sheet */
    private boolean looksLikePlayersSheet(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null)
            return false;

        int matchCount = 0;
        for (Cell cell : headerRow) {
            String val = normalize(getCellString(cell));
            if (matchesAny(val, H_NUMBER) || matchesAny(val, H_POSITION)
                    || matchesAny(val, H_FIRST_NAME) || matchesAny(val, H_LAST_NAME)
                    || matchesAny(val, H_FULL_NAME)) {
                matchCount++;
            }
        }
        return matchCount >= 2; // At least 2 player-like columns
    }

    // =====================================================
    // INTELLIGENT TEAM PARSING
    // =====================================================

    private TeamDTO parseTeamIntelligent(Sheet sheet) {
        // Try two layouts:
        // Layout A - Key-Value (2 columns): Header in col 0, Value in col 1
        // Layout B - Tabular: Headers in row 0, data in row 1

        Row firstRow = sheet.getRow(0);
        if (firstRow == null) {
            return buildDefaultTeam("Imported Team");
        }

        // Detect layout: if row 0 has headers and row 1 has data Ã¢â€ â€™ tabular
        // If col 0 looks like labels Ã¢â€ â€™ key-value
        boolean isKeyValue = isKeyValueLayout(sheet);

        if (isKeyValue) {
            return parseTeamKeyValue(sheet);
        } else {
            return parseTeamTabular(sheet);
        }
    }

    private boolean isKeyValueLayout(Sheet sheet) {
        // Check if the first few rows have exactly 2 cells with label-like first cell
        int kvCount = 0;
        for (int i = 0; i <= Math.min(5, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;
            String cell0 = getCellString(row.getCell(0)).toLowerCase().trim();
            if (matchesAny(normalize(cell0), H_TEAM_NAME) ||
                    matchesAny(normalize(cell0), H_TEAM_CATEGORY) ||
                    matchesAny(normalize(cell0), H_TEAM_LOGO) ||
                    matchesAny(normalize(cell0), H_TEAM_PRIMARY) ||
                    matchesAny(normalize(cell0), H_TEAM_SECONDARY)) {
                kvCount++;
            }
        }
        return kvCount >= 2;
    }

    private TeamDTO parseTeamKeyValue(Sheet sheet) {
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;
            String key = normalize(getCellString(row.getCell(0)));
            String value = getCellString(row.getCell(1));
            if (!key.isEmpty() && !value.isEmpty()) {
                data.put(key, value);
            }
        }

        String name = findValue(data, H_TEAM_NAME, DEFAULT_TEAM_NAME);
        String category = findValue(data, H_TEAM_CATEGORY, DEFAULT_AGE_CATEGORY);
        String logo = findValue(data, H_TEAM_LOGO, null);
        String primary = findValue(data, H_TEAM_PRIMARY, DEFAULT_PRIMARY);
        String secondary = findValue(data, H_TEAM_SECONDARY, DEFAULT_SECONDARY);
        String trim = findValue(data, H_TEAM_TRIM, DEFAULT_TRIM);

        return buildTeam(name, category, logo, primary, secondary, trim);
    }

    private TeamDTO parseTeamTabular(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        Row dataRow = sheet.getRow(1);
        if (headerRow == null || dataRow == null) {
            return buildDefaultTeam(DEFAULT_TEAM_NAME);
        }

        Map<String, Integer> colMap = buildColumnMap(headerRow);

        String name = getByHeaders(dataRow, colMap, H_TEAM_NAME, DEFAULT_TEAM_NAME);
        String category = getByHeaders(dataRow, colMap, H_TEAM_CATEGORY, DEFAULT_AGE_CATEGORY);
        String logo = getByHeaders(dataRow, colMap, H_TEAM_LOGO, null);
        String primary = getByHeaders(dataRow, colMap, H_TEAM_PRIMARY, DEFAULT_PRIMARY);
        String secondary = getByHeaders(dataRow, colMap, H_TEAM_SECONDARY, DEFAULT_SECONDARY);
        String trim = getByHeaders(dataRow, colMap, H_TEAM_TRIM, DEFAULT_TRIM);

        return buildTeam(name, category, logo, primary, secondary, trim);
    }

    private TeamDTO buildTeam(String name, String category, String logo,
            String primary, String secondary, String trim) {
        // Ensure hex format
        primary = ExcelUtils.ensureHex(primary, DEFAULT_PRIMARY);
        secondary = ExcelUtils.ensureHex(secondary, DEFAULT_SECONDARY);
        trim = ExcelUtils.ensureHex(trim, DEFAULT_TRIM);

        TeamDesignDTO design = TeamDesignDTO.builder()
                .style(WidgetAppearance.JERSEY)
                .jerseyDesign(JerseyDesign.SOLID)
                .logoIconName(logo == null ? "shield" : null)
                .logoFilePath(logo)
                .colors(TeamKitColorsDTO.builder()
                        .primaryHex(primary)
                        .secondaryHex(secondary)
                        .trimHex(trim)
                        .build())
                .build();

        return TeamDTO.builder()
                .name(name)
                .ageCategory(category)
                .design(design)
                .build();
    }

    private TeamDTO buildDefaultTeam(String name) {
        return buildTeam(name, DEFAULT_AGE_CATEGORY, null, DEFAULT_PRIMARY, DEFAULT_SECONDARY, DEFAULT_TRIM);
    }

    // =====================================================
    // INTELLIGENT PLAYERS PARSING
    // =====================================================

    private List<PlayerDTO> parsePlayersIntelligent(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            log.warn("Pas d'en-tête dans la feuille joueurs");
            return new ArrayList<>();
        }

        Map<String, Integer> colMap = buildColumnMap(headerRow);
        log.info("Colonnes détectées: {}", colMap);

        // Detect which name columns we have
        int firstNameCol = findColumn(colMap, H_FIRST_NAME);
        int lastNameCol = findColumn(colMap, H_LAST_NAME);
        int fullNameCol = findColumn(colMap, H_FULL_NAME);
        int numberCol = findColumn(colMap, H_NUMBER);
        int positionCol = findColumn(colMap, H_POSITION);
        int nationalityCol = findColumn(colMap, H_NATIONALITY);
        int categoryCol = findColumn(colMap, H_CATEGORY);
        int heightCol = findColumn(colMap, H_HEIGHT);
        int weightCol = findColumn(colMap, H_WEIGHT);
        int footCol = findColumn(colMap, H_FOOT);
        int emailCol = findColumn(colMap, H_EMAIL);
        int phoneCol = findColumn(colMap, H_PHONE);

        // If no name columns detected at all, try positional fallback
        boolean hasNameCols = firstNameCol >= 0 || lastNameCol >= 0 || fullNameCol >= 0;
        if (!hasNameCols) {
            log.warn("Aucune colonne de nom détectée, utilisation positionnelle");
            // Assume: col0=firstName/fullName, col1=lastName, col2=number, col3=position
            firstNameCol = 0;
            lastNameCol = headerRow.getLastCellNum() > 1 ? 1 : -1;
            if (numberCol < 0 && headerRow.getLastCellNum() > 2)
                numberCol = 2;
            if (positionCol < 0 && headerRow.getLastCellNum() > 3)
                positionCol = 3;
        }

        List<PlayerDTO> players = new ArrayList<>();
        Set<Integer> usedNumbers = new HashSet<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (isEmpty(row))
                continue;

            String firstName = null;
            String lastName = null;

            // Strategy 1: Separate first/last name columns
            if (firstNameCol >= 0)
                firstName = getString(row, firstNameCol, null);
            if (lastNameCol >= 0)
                lastName = getString(row, lastNameCol, null);

            // Strategy 2: Full name column Ã¢â€ â€™ split
            if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
                String fullName = fullNameCol >= 0 ? getString(row, fullNameCol, null) : null;
                if (fullName != null && !fullName.isBlank()) {
                    String[] parts = smartSplitName(fullName);
                    firstName = parts[0];
                    lastName = parts[1];
                }
            }

            // Strategy 3: Only one name column has data Ã¢â€ â€™ split it
            if (firstName != null && !firstName.isBlank() && (lastName == null || lastName.isBlank())) {
                String[] parts = smartSplitName(firstName);
                firstName = parts[0];
                lastName = parts[1];
            } else if ((firstName == null || firstName.isBlank()) && lastName != null && !lastName.isBlank()) {
                String[] parts = smartSplitName(lastName);
                firstName = parts[0];
                lastName = parts[1];
            }

            // Skip if still no name
            if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
                continue;
            }

            // Default the blank one
            if (firstName == null || firstName.isBlank())
                firstName = lastName;
            if (lastName == null || lastName.isBlank())
                lastName = firstName;

            Integer number = numberCol >= 0 ? getInt(row, numberCol) : null;
            String position = positionCol >= 0 ? getString(row, positionCol, DEFAULT_POSITION) : DEFAULT_POSITION;

            PlayerDTO.PlayerDTOBuilder builder = PlayerDTO.builder()
                    .firstName(firstName != null ? firstName.trim() : "")
                    .lastName(lastName != null ? lastName.trim() : "")
                    .number(number)
                    .mainPosition(PositionUtils.normalize(position));

            if (nationalityCol >= 0)
                builder.nationality(getString(row, nationalityCol, null));
            if (categoryCol >= 0)
                builder.category(getString(row, categoryCol, null));
            if (emailCol >= 0)
                builder.email(getString(row, emailCol, null));
            if (phoneCol >= 0)
                builder.phoneNumber(getString(row, phoneCol, null));
            if (heightCol >= 0) {
                Double h = getDouble(row, heightCol);
                if (h != null)
                    builder.heightCm(h);
            }
            if (weightCol >= 0) {
                Double w = getDouble(row, weightCol);
                if (w != null)
                    builder.weightKg(w);
            }
            if (footCol >= 0)
                builder.preferredFoot(getString(row, footCol, null));

            PlayerDTO player = builder.build();
            players.add(player);

            if (number != null)
                usedNumbers.add(number);
        }

        ExcelUtils.assignNumbers(players, usedNumbers);
        log.info("{} joueurs parsés avec succès", players.size());

        return players;
    }

    // =====================================================
    // INTELLIGENT STAFF PARSING
    // =====================================================

    private List<CoachDTO> parseStaffIntelligent(Sheet sheet) {
        List<CoachDTO> list = new ArrayList<>();
        if (sheet == null)
            return list;

        Row headerRow = sheet.getRow(0);
        if (headerRow == null)
            return list;

        Map<String, Integer> colMap = buildColumnMap(headerRow);

        int firstNameCol = findColumn(colMap, H_FIRST_NAME);
        int lastNameCol = findColumn(colMap, H_LAST_NAME);
        int fullNameCol = findColumn(colMap, H_FULL_NAME);
        int roleCol = findColumn(colMap, H_STAFF_ROLE);
        int nationalityCol = findColumn(colMap, H_NATIONALITY);

        // Fallback: positional
        boolean hasNameCols = firstNameCol >= 0 || lastNameCol >= 0 || fullNameCol >= 0;
        if (!hasNameCols) {
            firstNameCol = 0;
            lastNameCol = headerRow.getLastCellNum() > 1 ? 1 : -1;
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (isEmpty(row))
                continue;

            String firstName = null;
            String lastName = null;

            if (firstNameCol >= 0)
                firstName = getString(row, firstNameCol, "");
            if (lastNameCol >= 0)
                lastName = getString(row, lastNameCol, "");

            // Full name fallback
            if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
                String fullName = fullNameCol >= 0 ? getString(row, fullNameCol, null) : null;
                if (fullName != null && !fullName.isBlank()) {
                    String[] parts = smartSplitName(fullName);
                    firstName = parts[0];
                    lastName = parts[1];
                }
            }

            // Split if only one has data
            if (firstName != null && !firstName.isBlank() && (lastName == null || lastName.isBlank())) {
                String[] parts = smartSplitName(firstName);
                firstName = parts[0];
                lastName = parts[1];
            } else if ((firstName == null || firstName.isBlank()) && lastName != null && !lastName.isBlank()) {
                String[] parts = smartSplitName(lastName);
                firstName = parts[0];
                lastName = parts[1];
            }

            if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
                continue;
            }

            if (firstName == null || firstName.isBlank())
                firstName = "";
            if (lastName == null || lastName.isBlank())
                lastName = "";

            CoachDTO.CoachDTOBuilder builder = CoachDTO.builder()
                    .id(null)
                    .firstName(firstName.trim())
                    .lastName(lastName.trim())
                    .fullName((firstName.trim() + " " + lastName.trim()).trim())
                    .assigned(true);

            if (roleCol >= 0) {
            }
            if (nationalityCol >= 0) {
                builder.nationality(getString(row, nationalityCol, null));
            }

            list.add(builder.build());
        }

        log.info("{} membres du staff parsés", list.size());
        return list;
    }

}
