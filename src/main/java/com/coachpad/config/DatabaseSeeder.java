package com.coachpad.config;

import com.coachpad.persistence.Enum.*;
import com.coachpad.persistence.entity.*;
import com.coachpad.persistence.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DatabaseSeeder {

        @Bean
        CommandLineRunner initDatabase(TeamRepository teamRepository) {
                return args -> {
                        System.out.println("Checking database for existing teams before seeding...");

                        // --- REAL MADRID ---
                        if (!teamRepository.existsByName("Real Madrid")) {
                                teamRepository.save(createRealMadridSenior());
                                System.out.println("Seeded: Real Madrid Senior");
                        }

                        if (!teamRepository.existsByName("Real Madrid Castilla")) {
                                teamRepository.save(createRealMadridCastilla());
                                System.out.println("Seeded: Real Madrid Castilla");
                        }

                        if (!teamRepository.existsByName("Real Madrid Juvenil A")) {
                                teamRepository.save(createRealMadridJuvenilA());
                                System.out.println("Seeded: Real Madrid Juvenil A");
                        }

                        // --- PSG ---
                        if (!teamRepository.existsByName("Paris Saint-Germain")) {
                                teamRepository.save(createPSGSenior());
                                System.out.println("Seeded: Paris Saint-Germain (PSG Senior)");
                        }

                        if (!teamRepository.existsByName("PSG U19")) {
                                teamRepository.save(createPSGU19());
                                System.out.println("Seeded: PSG U19");
                        }

                        if (!teamRepository.existsByName("PSG U17")) {
                                teamRepository.save(createPSGU17());
                                System.out.println("Seeded: PSG U17");
                        }

                        System.out.println("Database seeding check completed.");
                };
        }

        private TeamEntity createRealMadridSenior() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder()
                                .primaryHex("#FFFFFF").secondaryHex("#000000").build();
                TeamDesignEntity design = TeamDesignEntity.builder()
                                .style(WidgetAppearance.CIRCLE).jerseyDesign(JerseyDesign.SOLID).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/5/56/Real_Madrid_CF.svg/1200px-Real_Madrid_CF.svg.png")
                                .usePlayerPhotos(true).build();
                TeamEntity team = TeamEntity.builder()
                                .name("Real Madrid").nickname("Los Blancos").ageCategory("SENIOR").design(design)
                                .build();
                colors.setDesign(design);
                design.setTeam(team);

                // Staff Technique
                team.addCoach(createCoach("Carlo", "Ancelotti", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.TACTICAL_FLEXIBILITY,
                                "https://b.fssta.com/uploads/application/soccer/headshots/2107.png"));
                team.addCoach(createCoach("Davide", "Ancelotti", CoachRole.ASSISTANT_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.TACTICAL_FLEXIBILITY, ""));
                team.addCoach(createCoach("Antonio", "Pintus", CoachRole.FITNESS_COACH, LicenseLevel.NONE,
                                CoachingPhilosophy.BALANCED, ""));

                // Gardiens
                team.addPlayer(createPlayer("Thibaut", "Courtois", 1, "GK", "Belgium", "1992-05-11",
                                "https://b.fssta.com/uploads/application/soccer/headshots/711.png"));
                team.addPlayer(createPlayer("Andriy", "Lunin", 13, "GK", "Ukraine", "1999-02-11",
                                "https://b.fssta.com/uploads/application/soccer/headshots/53635.png"));
                team.addPlayer(createPlayer("Fran", "González", 25, "GK", "Spain", "2005-06-24",
                                "https://b.fssta.com/uploads/application/soccer/headshots/101037.png"));

                // Défenseurs
                team.addPlayer(createPlayer("Dani", "Carvajal", 2, "RB", "Spain", "1992-01-11",
                                "https://b.fssta.com/uploads/application/soccer/headshots/2029.png"));
                team.addPlayer(createPlayer("Éder", "Militão", 3, "CB", "Brazil", "1998-01-18",
                                "https://b.fssta.com/uploads/application/soccer/headshots/45402.png"));
                team.addPlayer(createPlayer("David", "Alaba", 4, "CB", "Austria", "1992-06-24",
                                "https://b.fssta.com/uploads/application/soccer/headshots/2022.png"));
                team.addPlayer(createPlayer("Antonio", "Rüdiger", 22, "CB", "Germany", "1993-03-03",
                                "https://b.fssta.com/uploads/application/soccer/headshots/2330.png"));
                team.addPlayer(createPlayer("Ferland", "Mendy", 23, "LB", "France", "1995-06-08",
                                "https://b.fssta.com/uploads/application/soccer/headshots/54266.png"));
                team.addPlayer(createPlayer("Lucas", "Vázquez", 17, "RB", "Spain", "1991-07-01",
                                "https://b.fssta.com/uploads/application/soccer/headshots/2030.png"));
                team.addPlayer(createPlayer("Fran", "García", 20, "LB", "Spain", "1999-08-14",
                                "https://b.fssta.com/uploads/application/soccer/headshots/60109.png"));
                team.addPlayer(createPlayer("Jesús", "Vallejo", 18, "CB", "Spain", "1997-01-05",
                                "https://b.fssta.com/uploads/application/soccer/headshots/2021.png"));

                // Milieux
                team.addPlayer(createPlayer("Jude", "Bellingham", 5, "CAM", "England", "2003-06-29",
                                "https://b.fssta.com/uploads/application/soccer/headshots/71310.png"));
                team.addPlayer(createPlayer("Eduardo", "Camavinga", 6, "CM", "France", "2002-11-10",
                                "https://b.fssta.com/uploads/application/soccer/headshots/69367.png"));
                team.addPlayer(createPlayer("Federico", "Valverde", 8, "CM", "Uruguay", "1998-07-22",
                                "https://b.fssta.com/uploads/application/soccer/headshots/42653.png"));
                team.addPlayer(createPlayer("Luka", "Modric", 10, "CM", "Croatia", "1985-09-09",
                                "https://b.fssta.com/uploads/application/soccer/headshots/880.png"));
                team.addPlayer(createPlayer("Aurélien", "Tchouaméni", 14, "CDM", "France", "2000-01-27",
                                "https://b.fssta.com/uploads/application/soccer/headshots/58145.png"));
                team.addPlayer(createPlayer("Arda", "Güler", 15, "AM", "Turkey", "2005-02-25",
                                "https://b.fssta.com/uploads/application/soccer/headshots/90609.png"));
                team.addPlayer(createPlayer("Dani", "Ceballos", 19, "CM", "Spain", "1996-08-07",
                                "https://b.fssta.com/uploads/application/soccer/headshots/31338.png"));

                // Attaquants
                team.addPlayer(createPlayer("Vinícius", "Júnior", 7, "LW", "Brazil", "2000-07-12",
                                "https://b.fssta.com/uploads/application/soccer/headshots/53004.png"));
                team.addPlayer(createPlayer("Kylian", "Mbappé", 9, "ST", "France", "1998-12-20",
                                "https://b.fssta.com/uploads/application/soccer/headshots/40670.png"));
                team.addPlayer(createPlayer("Rodrygo", "Goes", 11, "RW", "Brazil", "2001-01-09",
                                "https://b.fssta.com/uploads/application/soccer/headshots/67951.png"));
                team.addPlayer(createPlayer("Endrick", "Felipe", 16, "ST", "Brazil", "2006-07-21",
                                "https://b.fssta.com/uploads/application/soccer/headshots/113245.png"));
                team.addPlayer(createPlayer("Brahim", "Díaz", 21, "RW", "Morocco", "1999-08-03",
                                "https://b.fssta.com/uploads/application/soccer/headshots/41810.png"));

                return team;
        }

        private TeamEntity createRealMadridCastilla() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#FFFFFF").secondaryHex("#000080")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.SOLID).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/5/56/Real_Madrid_CF.svg/1200px-Real_Madrid_CF.svg.png")
                                .usePlayerPhotos(true).build();
                TeamEntity team = TeamEntity.builder().name("Real Madrid Castilla").nickname("La Fábrica")
                                .ageCategory("U21")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("Raúl", "González", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT,
                                "https://b.fssta.com/uploads/application/soccer/headshots/2026.png"));
                team.addPlayer(createPlayer("Fran", "González", 25, "GK", "Spain", "2005-06-24", ""));
                team.addPlayer(createPlayer("Raúl", "Asencio", 4, "CB", "Spain", "2003-02-13", ""));
                team.addPlayer(createPlayer("Manuel", "Ángel", 8, "CM", "Spain", "2004-03-15", ""));
                team.addPlayer(createPlayer("Gonzalo", "García", 9, "ST", "Spain", "2004-03-15", ""));

                return team;
        }

        private TeamEntity createRealMadridJuvenilA() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#FFFFFF").secondaryHex("#800080")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.SOLID).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/5/56/Real_Madrid_CF.svg/1200px-Real_Madrid_CF.svg.png")
                                .usePlayerPhotos(false).build();
                TeamEntity team = TeamEntity.builder().name("Real Madrid Juvenil A").nickname("Juveniles")
                                .ageCategory("U19")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("Álvaro", "Arbeloa", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT,
                                "https://b.fssta.com/uploads/application/soccer/headshots/2028.png"));
                team.addPlayer(createPlayer("Joan", "Martínez", 5, "CB", "Spain", "2007-08-20", ""));
                team.addPlayer(createPlayer("Pol", "Fortuny", 10, "CAM", "Spain", "2005-03-11", ""));

                return team;
        }

        private TeamEntity createPSGSenior() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#004170").secondaryHex("#DA291C")
                                .trimHex("#FFFFFF").build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.STRIPED_VERTICAL).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/a/a7/Paris_Saint-Germain_F.C..svg/1200px-Paris_Saint-Germain_F.C..svg.png")
                                .usePlayerPhotos(true).build();
                TeamEntity team = TeamEntity.builder().name("Paris Saint-Germain").nickname("PSG").ageCategory("SENIOR")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                // Staff Technique
                team.addCoach(createCoach("Luis", "Enrique", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.TIKI_TAKA,
                                "https://b.fssta.com/uploads/application/soccer/headshots/2357.png"));
                team.addCoach(createCoach("Rafel", "Pol", CoachRole.ASSISTANT_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.POSSESSION, ""));

                // Gardiens
                team.addPlayer(createPlayer("Gianluigi", "Donnarumma", 1, "GK", "Italy", "1999-02-25",
                                "https://b.fssta.com/uploads/application/soccer/headshots/31875.png"));
                team.addPlayer(createPlayer("Matvey", "Safonov", 39, "GK", "Russia", "1999-02-25",
                                "https://b.fssta.com/uploads/application/soccer/headshots/65305.png"));
                team.addPlayer(createPlayer("Arnau", "Tenas", 80, "GK", "Spain", "2001-05-30",
                                "https://b.fssta.com/uploads/application/soccer/headshots/113458.png"));

                // Défenseurs
                team.addPlayer(createPlayer("Achraf", "Hakimi", 2, "RB", "Morocco", "1998-11-04",
                                "https://b.fssta.com/uploads/application/soccer/headshots/44920.png"));
                team.addPlayer(createPlayer("Marquinhos", "Aoás Corrêa", 5, "CB", "Brazil", "1994-05-14",
                                "https://b.fssta.com/uploads/application/soccer/headshots/75573.png"));
                team.addPlayer(createPlayer("Lucas", "Hernández", 21, "CB", "France", "1996-02-14",
                                "https://b.fssta.com/uploads/application/soccer/headshots/1104.png"));
                team.addPlayer(createPlayer("Willian", "Pacho", 51, "CB", "Ecuador", "2001-10-16",
                                "https://b.fssta.com/uploads/application/soccer/headshots/113456.png"));
                team.addPlayer(createPlayer("Lucas", "Beraldo", 35, "CB", "Brazil", "2003-11-24",
                                "https://b.fssta.com/uploads/application/soccer/headshots/90475.png"));
                team.addPlayer(createPlayer("Nuno", "Mendes", 25, "LB", "Portugal", "2002-06-19",
                                "https://b.fssta.com/uploads/application/soccer/headshots/74576.png"));
                team.addPlayer(createPlayer("Milan", "Škriniar", 37, "CB", "Slovakia", "1995-02-11",
                                "https://b.fssta.com/uploads/application/soccer/headshots/42188.png"));
                team.addPlayer(createPlayer("Yoram", "Zague", 42, "RB", "France", "2006-05-15",
                                "https://b.fssta.com/uploads/application/soccer/headshots/113459.png"));

                // Milieux
                team.addPlayer(createPlayer("Vitinha", "Machado", 17, "CM", "Portugal", "2000-02-13",
                                "https://b.fssta.com/uploads/application/soccer/headshots/68735.png"));
                team.addPlayer(createPlayer("Warren", "Zaïre-Emery", 33, "CM", "France", "2006-03-08",
                                "https://b.fssta.com/uploads/application/soccer/headshots/106915.png"));
                team.addPlayer(createPlayer("Fabián", "Ruiz", 8, "CM", "Spain", "1996-04-03",
                                "https://b.fssta.com/uploads/application/soccer/headshots/30456.png"));
                team.addPlayer(createPlayer("João", "Neves", 87, "CM", "Portugal", "2004-09-27",
                                "https://b.fssta.com/uploads/application/soccer/headshots/115678.png"));
                team.addPlayer(createPlayer("Senny", "Mayulu", 24, "CAM", "France", "2006-05-17",
                                "https://b.fssta.com/uploads/application/soccer/headshots/113460.png"));

                // Attaquants
                team.addPlayer(createPlayer("Ousmane", "Dembélé", 10, "RW", "France", "1997-05-15",
                                "https://b.fssta.com/uploads/application/soccer/headshots/39732.png"));
                team.addPlayer(createPlayer("Bradley", "Barcola", 29, "LW", "France", "2002-09-02",
                                "https://b.fssta.com/uploads/application/soccer/headshots/95034.png"));
                team.addPlayer(createPlayer("Marco", "Asensio", 11, "CAM", "Spain", "1996-01-21",
                                "https://b.fssta.com/uploads/application/soccer/headshots/7199.png"));
                team.addPlayer(createPlayer("Lee", "Kang-in", 19, "RW", "South Korea", "2001-02-19",
                                "https://b.fssta.com/uploads/application/soccer/headshots/60601.png"));
                team.addPlayer(createPlayer("Gonçalo", "Ramos", 9, "ST", "Portugal", "2001-06-20",
                                "https://b.fssta.com/uploads/application/soccer/headshots/74559.png"));
                team.addPlayer(createPlayer("Randal", "Kolo Muani", 23, "ST", "France", "1998-12-05",
                                "https://b.fssta.com/uploads/application/soccer/headshots/50949.png"));

                return team;
        }

        private TeamEntity createPSGU19() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#004170").secondaryHex("#DA291C")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.STRIPED_VERTICAL).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/a/a7/Paris_Saint-Germain_F.C..svg/1200px-Paris_Saint-Germain_F.C..svg.png")
                                .usePlayerPhotos(false).build();
                TeamEntity team = TeamEntity.builder().name("PSG U19").nickname("Titis Parisiens").ageCategory("U19")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("Zoumana", "Camara", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT,
                                "https://b.fssta.com/uploads/application/soccer/headshots/17281.png"));
                team.addPlayer(createPlayer("Senny", "Mayulu", 10, "CAM", "France", "2006-05-17", ""));
                team.addPlayer(createPlayer("Yoram", "Zague", 2, "RB", "France", "2006-05-15", ""));

                return team;
        }

        private TeamEntity createPSGU17() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#004170").secondaryHex("#DA291C")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.STRIPED_VERTICAL).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/a/a7/Paris_Saint-Germain_F.C..svg/1200px-Paris_Saint-Germain_F.C..svg.png")
                                .usePlayerPhotos(false).build();
                TeamEntity team = TeamEntity.builder().name("PSG U17").nickname("Espoirs").ageCategory("U17")
                                .design(design)
                                .build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("David", "Suarez", CoachRole.HEAD_COACH, LicenseLevel.UEFA_A,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT, ""));
                team.addPlayer(createPlayer("Adam", "Ayari", 7, "RW", "France", "2008-01-01", ""));

                return team;
        }

        private CoachEntity createCoach(String fn, String ln, CoachRole role, LicenseLevel level,
                        CoachingPhilosophy phil,
                        String photo) {
                return CoachEntity.builder()
                                .firstName(fn).lastName(ln).fullName(fn + " " + ln)
                                .role(role).licenseLevel(level).coachingPhilosophy(phil)
                                .photoUrl(photo).build();
        }

        private PlayerEntity createPlayer(String fn, String ln, int num, String pos, String nat, String dob,
                        String photo) {
                // Fallback for missing last name and position to pass @NotBlank validation
                String finalLn = (ln == null || ln.isBlank()) ? fn : ln;
                String finalPos = (pos == null || pos.isBlank()) ? "TBD" : pos;
                int finalNum = (num < 1) ? 99 : num;

                return PlayerEntity.builder()
                                .firstName(fn).lastName(finalLn)
                                .fullName((fn + " " + (ln != null ? ln : "")).trim())
                                .number(finalNum).mainPosition(finalPos).nationality(nat)
                                .dateOfBirth((dob == null || dob.isEmpty()) ? null : LocalDate.parse(dob))
                                .photoUrl(photo).build();
        }
}
