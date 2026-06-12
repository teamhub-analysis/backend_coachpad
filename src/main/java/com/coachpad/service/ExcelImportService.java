package com.coachpad.service;

import com.coachpad.dto.*;
import com.coachpad.model.enums.JerseyDesign;
import com.coachpad.model.enums.WidgetAppearance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class ExcelImportService {

    // =====================================================
    // Aliases for intelligent sheet detection
    // =====================================================

    private static final List<String> TEAM_ALIASES = List.of(
            "team", "equipe", "équipe", "club", "info", "infos", "informations",
            "team info", "team_info", "fiche", "general", "général", "generale",
            "presentation", "présentation", "données", "donnees", "data");

    private static final List<String> PLAYERS_ALIASES = List.of(
            "players", "joueurs", "effectif", "roster", "squad", "player",
            "joueur", "liste joueurs", "liste_joueurs", "list", "liste",
            "footballers", "footballeurs", "equipe joueurs", "player list");

    private static final List<String> TECH_STAFF_ALIASES = List.of(
            "technical staff", "staff technique", "staff tech", "staff_technique",
            "coaching staff", "coaches", "coachs", "entraineurs", "entraîneurs",
            "trainers", "technical", "technique", "encadrement", "encadrement technique",
            "staff", "coaching", "coach");

    private static final List<String> MEDICAL_STAFF_ALIASES = List.of(
            "medical staff", "staff medical", "staff médical", "staff_medical",
            "medical", "médical", "medecins", "médecins", "doctors", "medical team",
            "equipe medicale", "équipe médicale", "soins", "sante", "santé",
            "paramedical", "paramédical", "kine", "kiné");

    // =====================================================
    // Aliases for intelligent column/header detection
    // =====================================================

    // -- Team headers --
    private static final List<String> H_TEAM_NAME = List.of(
            "name", "nom", "team name", "nom equipe", "nom équipe", "club",
            "team", "equipe", "équipe", "nom du club", "nom_equipe");

    private static final List<String> H_TEAM_CATEGORY = List.of(
            "category", "catégorie", "categorie", "age category", "age", "cat",
            "categorie age", "catégorie d'âge", "tranche", "niveau");

    private static final List<String> H_TEAM_LOGO = List.of(
            "logo", "logo url", "logo_url", "logo path", "image", "emblem",
            "ecusson", "écusson", "blason", "badge", "crest", "icon", "icone", "icône");

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
            "firstname", "first name", "first_name", "prenom", "prénom",
            "nom de bapteme", "given name", "fname");

    private static final List<String> H_LAST_NAME = List.of(
            "lastname", "last name", "last_name", "nom", "nom de famille",
            "family name", "surname", "lname", "name");

    private static final List<String> H_FULL_NAME = List.of(
            "fullname", "full name", "full_name", "nom complet", "nom_complet",
            "joueur", "player", "nom et prenom", "nom et prénom",
            "nom & prenom", "nom & prénom");

    private static final List<String> H_NUMBER = List.of(
            "number", "numero", "numéro", "num", "n°", "no", "n",
            "shirt", "shirt number", "maillot", "jersey", "jersey number",
            "dorsal", "dossard", "#");

    private static final List<String> H_POSITION = List.of(
            "position", "poste", "pos", "role", "rôle",
            "main position", "position principale", "poste principal");

    private static final List<String> H_NATIONALITY = List.of(
            "nationality", "nationalité", "nationalite", "nat", "country",
            "pays", "nation", "origin", "origine");

    private static final List<String> H_DOB = List.of(
            "dob", "date of birth", "date_of_birth", "dateofbirth",
            "date de naissance", "date_naissance", "naissance", "birthday",
            "birth date", "birthdate", "born", "né le", "ne le", "age");

    private static final List<String> H_HEIGHT = List.of(
            "height", "taille", "hauteur", "height cm", "heightcm",
            "taille cm", "taille_cm", "cm");

    private static final List<String> H_WEIGHT = List.of(
            "weight", "poids", "weight kg", "weightkg",
            "poids kg", "poids_kg", "kg", "masse");

    private static final List<String> H_FOOT = List.of(
            "foot", "pied", "preferred foot", "pied préféré", "pied prefere",
            "pied fort", "strong foot", "dominant foot");

    private static final List<String> H_CATEGORY = List.of(
            "category", "catégorie", "categorie", "cat",
            "player category", "catégorie joueur");

    private static final List<String> H_EMAIL = List.of(
            "email", "e-mail", "mail", "courriel", "adresse email",
            "adresse mail", "email address");

    private static final List<String> H_PHONE = List.of(
            "phone", "telephone", "téléphone", "tel", "tél",
            "phone number", "mobile", "gsm", "portable", "numéro tel");

    // -- Staff headers --
    private static final List<String> H_STAFF_ROLE = List.of(
            "role", "rôle", "fonction", "function", "poste", "position",
            "titre", "title", "job", "spécialité", "specialite", "speciality");

    // =====================================================
    // MAIN ENTRY POINT
    // =====================================================

    public TeamDTO importFullTeam(MultipartFile file) {
        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            int sheetCount = workbook.getNumberOfSheets();
            System.out.println("📊 Excel contient " + sheetCount + " feuille(s):");
            for (int i = 0; i < sheetCount; i++) {
                System.out.println("   📄 [" + i + "] " + workbook.getSheetName(i));
            }

            // --- INTELLIGENT SHEET DETECTION ---
            Sheet teamSheet = findSheet(workbook, TEAM_ALIASES);
            Sheet playersSheet = findSheet(workbook, PLAYERS_ALIASES);
            Sheet staffSheet = findSheet(workbook, TECH_STAFF_ALIASES);
            Sheet medicalSheet = findSheet(workbook, MEDICAL_STAFF_ALIASES);

            System.out.println("🔍 Détection des feuilles:");
            System.out.println("   Team:     " + (teamSheet != null ? teamSheet.getSheetName() : "❌ non trouvée"));
            System.out
                    .println("   Players:  " + (playersSheet != null ? playersSheet.getSheetName() : "❌ non trouvée"));
            System.out.println("   Staff:    " + (staffSheet != null ? staffSheet.getSheetName() : "❌ non trouvée"));
            System.out
                    .println("   Medical:  " + (medicalSheet != null ? medicalSheet.getSheetName() : "❌ non trouvée"));

            // --- FALLBACK: If only 1 sheet, treat it as players ---
            if (playersSheet == null && sheetCount == 1) {
                playersSheet = workbook.getSheetAt(0);
                System.out.println("   ℹ️ Fichier avec une seule feuille → utilisée comme joueurs");
            }

            // --- FALLBACK: If no player sheet found, try unmatched sheets ---
            if (playersSheet == null) {
                for (int i = 0; i < sheetCount; i++) {
                    Sheet s = workbook.getSheetAt(i);
                    if (s != teamSheet && s != staffSheet && s != medicalSheet) {
                        // Check if this sheet looks like a players sheet (has number/position columns)
                        if (looksLikePlayersSheet(s)) {
                            playersSheet = s;
                            System.out.println(
                                    "   ℹ️ Feuille '" + s.getSheetName() + "' détectée comme joueurs par contenu");
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
                        : "Imported Team";
                team = TeamDTO.builder()
                        .name(teamName)
                        .ageCategory("Senior")
                        .design(TeamDesignDTO.builder()
                                .style(WidgetAppearance.JERSEY)
                                .jerseyDesign(JerseyDesign.SOLID)
                                .logoIconName("shield")
                                .colors(TeamKitColorsDTO.builder()
                                        .primaryHex("#FFFFFF")
                                        .secondaryHex("#000000")
                                        .trimHex("#FF0000")
                                        .build())
                                .build())
                        .build();
                System.out.println("   ℹ️ Pas de feuille Team → nom déduit du fichier: " + teamName);
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
                throw new RuntimeException("Aucune donnée trouvée dans le fichier Excel");
            }

            // Validate players (allow empty if we have staff)
            if (!players.isEmpty()) {
                validatePlayers(players);
            }

            // --- BUILD FINAL ---
            team.setPlayers(players);
            team.setCoaches(coaches);
            team.setMedicalStaff(medical);
            team.setSource("EXCEL");
            team.setImportFileName(file.getOriginalFilename());

            System.out.println("✅ Import terminé:");
            System.out.println("   ⚽ " + players.size() + " joueurs");
            System.out.println("   👔 " + coaches.size() + " staff technique");
            System.out.println("   🏥 " + medical.size() + " staff médical");

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

        // Detect layout: if row 0 has headers and row 1 has data → tabular
        // If col 0 looks like labels → key-value
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

        String name = findValue(data, H_TEAM_NAME, "Imported Team");
        String category = findValue(data, H_TEAM_CATEGORY, "Senior");
        String logo = findValue(data, H_TEAM_LOGO, null);
        String primary = findValue(data, H_TEAM_PRIMARY, "#FFFFFF");
        String secondary = findValue(data, H_TEAM_SECONDARY, "#000000");
        String trim = findValue(data, H_TEAM_TRIM, "#FF0000");

        return buildTeam(name, category, logo, primary, secondary, trim);
    }

    private TeamDTO parseTeamTabular(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        Row dataRow = sheet.getRow(1);
        if (headerRow == null || dataRow == null) {
            return buildDefaultTeam("Imported Team");
        }

        Map<String, Integer> colMap = buildColumnMap(headerRow);

        String name = getByHeaders(dataRow, colMap, H_TEAM_NAME, "Imported Team");
        String category = getByHeaders(dataRow, colMap, H_TEAM_CATEGORY, "Senior");
        String logo = getByHeaders(dataRow, colMap, H_TEAM_LOGO, null);
        String primary = getByHeaders(dataRow, colMap, H_TEAM_PRIMARY, "#FFFFFF");
        String secondary = getByHeaders(dataRow, colMap, H_TEAM_SECONDARY, "#000000");
        String trim = getByHeaders(dataRow, colMap, H_TEAM_TRIM, "#FF0000");

        return buildTeam(name, category, logo, primary, secondary, trim);
    }

    private TeamDTO buildTeam(String name, String category, String logo,
            String primary, String secondary, String trim) {
        // Ensure hex format
        primary = ensureHex(primary, "#FFFFFF");
        secondary = ensureHex(secondary, "#000000");
        trim = ensureHex(trim, "#FF0000");

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
        return buildTeam(name, "Senior", null, "#FFFFFF", "#000000", "#FF0000");
    }

    // =====================================================
    // INTELLIGENT PLAYERS PARSING
    // =====================================================

    private List<PlayerDTO> parsePlayersIntelligent(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            System.out.println("⚠️ Pas d'en-tête dans la feuille joueurs");
            return new ArrayList<>();
        }

        Map<String, Integer> colMap = buildColumnMap(headerRow);
        System.out.println("   📋 Colonnes détectées: " + colMap);

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
            System.out.println("   ⚠️ Aucune colonne de nom détectée, utilisation positionnelle");
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

            // Strategy 2: Full name column → split
            if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
                String fullName = fullNameCol >= 0 ? getString(row, fullNameCol, null) : null;
                if (fullName != null && !fullName.isBlank()) {
                    String[] parts = smartSplitName(fullName);
                    firstName = parts[0];
                    lastName = parts[1];
                }
            }

            // Strategy 3: Only one name column has data → split it
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
            String position = positionCol >= 0 ? getString(row, positionCol, "TBD") : "TBD";

            PlayerDTO.PlayerDTOBuilder builder = PlayerDTO.builder()
                    .firstName(firstName.trim())
                    .lastName(lastName.trim())
                    .number(number)
                    .mainPosition(normalizePosition(position));

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

        assignNumbers(players, usedNumbers);
        System.out.println("   ⚽ " + players.size() + " joueurs parsés avec succès");

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
                String role = getString(row, roleCol, null);
                // We could map roles here if needed
            }
            if (nationalityCol >= 0) {
                builder.nationality(getString(row, nationalityCol, null));
            }

            list.add(builder.build());
        }

        System.out.println("   👔 " + list.size() + " membres du staff parsés");
        return list;
    }

    // =====================================================
    // COLUMN DETECTION HELPERS
    // =====================================================

    /** Build a map of normalized header name → column index */
    private Map<String, Integer> buildColumnMap(Row headerRow) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Cell cell : headerRow) {
            String value = normalize(getCellString(cell));
            if (!value.isEmpty()) {
                map.put(value, cell.getColumnIndex());
            }
        }
        return map;
    }

    /** Find the column index for a given set of header aliases */
    private int findColumn(Map<String, Integer> colMap, List<String> aliases) {
        // 1. Exact match
        for (Map.Entry<String, Integer> entry : colMap.entrySet()) {
            for (String alias : aliases) {
                if (entry.getKey().equals(normalize(alias))) {
                    return entry.getValue();
                }
            }
        }
        // 2. Contains match
        for (Map.Entry<String, Integer> entry : colMap.entrySet()) {
            for (String alias : aliases) {
                String normAlias = normalize(alias);
                if (entry.getKey().contains(normAlias) || normAlias.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return -1; // Not found
    }

    /** Get cell value by trying multiple header aliases */
    private String getByHeaders(Row row, Map<String, Integer> colMap,
            List<String> aliases, String defaultValue) {
        int col = findColumn(colMap, aliases);
        if (col >= 0) {
            return getString(row, col, defaultValue);
        }
        return defaultValue;
    }

    /** Find value in a key-value map by trying multiple aliases */
    private String findValue(Map<String, String> data, List<String> aliases, String defaultValue) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            for (String alias : aliases) {
                if (entry.getKey().equals(normalize(alias)) || entry.getKey().contains(normalize(alias))) {
                    return entry.getValue();
                }
            }
        }
        return defaultValue;
    }

    private boolean matchesAny(String value, List<String> aliases) {
        for (String alias : aliases) {
            String normAlias = normalize(alias);
            if (value.equals(normAlias) || value.contains(normAlias) || normAlias.contains(value)) {
                return true;
            }
        }
        return false;
    }

    // =====================================================
    // NUMBER ASSIGNMENT
    // =====================================================

    private void assignNumbers(List<PlayerDTO> players, Set<Integer> used) {
        Set<Integer> assigned = new HashSet<>();
        int next = 1;

        for (PlayerDTO p : players) {
            Integer num = p.getNumber();

            if (num == null || num < 1 || num > 99 || assigned.contains(num)) {
                while ((used.contains(next) || assigned.contains(next)) && next <= 99) {
                    next++;
                }
                if (next > 99)
                    next = 1; // wrap around rather than crash
                p.setNumber(next);
                assigned.add(next);
            } else {
                assigned.add(num);
            }
        }
    }

    // =====================================================
    // VALIDATION
    // =====================================================

    private void validatePlayers(List<PlayerDTO> players) {
        // Remove invalid players instead of crashing
        players.removeIf(p -> (p.getFirstName() == null || p.getFirstName().isBlank())
                && (p.getLastName() == null || p.getLastName().isBlank()));

        if (players.isEmpty()) {
            throw new RuntimeException("Aucun joueur valide trouvé dans le fichier");
        }

        // Fix missing names
        for (PlayerDTO p : players) {
            if (p.getFirstName() == null || p.getFirstName().isBlank()) {
                p.setFirstName(p.getLastName());
            }
            if (p.getLastName() == null || p.getLastName().isBlank()) {
                p.setLastName(p.getFirstName());
            }
        }
    }

    // =====================================================
    // NAME PARSING
    // =====================================================

    /** Intelligently split a full name into [firstName, lastName] */
    private String[] smartSplitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[] { "", "" };
        }

        fullName = fullName.trim();

        // Handle "LAST, First" format
        if (fullName.contains(",")) {
            String[] parts = fullName.split(",", 2);
            return new String[] { parts[1].trim(), parts[0].trim() };
        }

        // Handle "First LAST" or "First Last" format
        String[] parts = fullName.split("\\s+", 2);
        if (parts.length == 1) {
            return new String[] { parts[0], parts[0] };
        }
        return new String[] { parts[0], parts[1] };
    }

    // =====================================================
    // POSITION NORMALIZATION
    // =====================================================

    private String normalizePosition(String pos) {
        if (pos == null || pos.isBlank())
            return "TBD";
        String p = pos.trim().toUpperCase();

        // Goalkeeper
        if (p.matches(".*\\b(GK|GB|GOAL|GARDIEN|KEEPER|GOALKEEPER|PORTERO|PORTIERE)\\b.*"))
            return "GK";
        // Defender
        if (p.matches(
                ".*\\b(DEF|DC|DG|DD|CB|LB|RB|BACK|DEFENDER|DÉFENSEUR|DEFENSEUR|ARRIERE|ARRIÈRE|LATÉRAL|LATERAL)\\b.*"))
            return mapDefenderPosition(p);
        // Midfielder
        if (p.matches(".*\\b(MID|MC|MG|MD|MDC|MOC|CM|CDM|CAM|LM|RM|MILIEU|MIDFIELDER|MEDIO)\\b.*"))
            return mapMidfielderPosition(p);
        // Forward
        if (p.matches(".*\\b(FW|ATT|ST|CF|LW|RW|FORWARD|ATTAQUANT|STRIKER|AILIER|AVANT|DELANTERO|PUNTA)\\b.*"))
            return mapForwardPosition(p);

        return pos.trim(); // Return as-is if unrecognized
    }

    private String mapDefenderPosition(String p) {
        if (p.contains("DG") || p.contains("LB") || p.contains("GAUCHE") || p.contains("LEFT"))
            return "LB";
        if (p.contains("DD") || p.contains("RB") || p.contains("DROIT") || p.contains("RIGHT"))
            return "RB";
        if (p.contains("DC") || p.contains("CB") || p.contains("CENTRAL"))
            return "CB";
        return "CB";
    }

    private String mapMidfielderPosition(String p) {
        if (p.contains("MDC") || p.contains("CDM") || p.contains("DEFENSIF") || p.contains("DÉFENSIF"))
            return "CDM";
        if (p.contains("MOC") || p.contains("CAM") || p.contains("OFFENSIF"))
            return "CAM";
        if (p.contains("MG") || p.contains("LM") || p.contains("GAUCHE"))
            return "LM";
        if (p.contains("MD") || p.contains("RM") || p.contains("DROIT"))
            return "RM";
        return "CM";
    }

    private String mapForwardPosition(String p) {
        if (p.contains("LW") || p.contains("AG") || p.contains("AILIER") && p.contains("GAUCHE"))
            return "LW";
        if (p.contains("RW") || p.contains("AD") || p.contains("AILIER") && p.contains("DROIT"))
            return "RW";
        if (p.contains("ST") || p.contains("CF") || p.contains("AVANT") || p.contains("POINTE"))
            return "ST";
        return "ST";
    }

    // =====================================================
    // UTILITY HELPERS
    // =====================================================

    /**
     * Normalize a string for comparison: lowercase, trim, remove accents and
     * special chars
     */
    private String normalize(String s) {
        if (s == null)
            return "";
        return java.text.Normalizer.normalize(s.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^a-z0-9 ]", "")
                .trim();
    }

    /** Ensure a string looks like a hex color */
    private String ensureHex(String value, String defaultHex) {
        if (value == null || value.isBlank())
            return defaultHex;
        value = value.trim();
        if (!value.startsWith("#"))
            value = "#" + value;
        if (value.matches("#[0-9A-Fa-f]{3,8}"))
            return value;
        return defaultHex;
    }

    /** Get the string value of a cell */
    private String getCellString(Cell cell) {
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

    private String getString(Row row, int index, String def) {
        if (row == null)
            return def;
        Cell cell = row.getCell(index);
        if (cell == null)
            return def;

        String value = getCellString(cell);
        return (value.isEmpty()) ? def : value;
    }

    private Integer getInt(Row row, int index) {
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

    private Double getDouble(Row row, int index) {
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

    private boolean isEmpty(Row row) {
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
}
