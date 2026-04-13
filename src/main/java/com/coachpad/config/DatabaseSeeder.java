package com.coachpad.config;

import com.coachpad.persistence.Enum.*;
import com.coachpad.persistence.entity.*;
import com.coachpad.persistence.repository.TeamRepository;
import com.coachpad.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder {

        private final PasswordEncoder passwordEncoder;

        @Bean
        CommandLineRunner initDatabase(TeamRepository teamRepository, UserRepository userRepository) {
                return args -> {
                        System.out.println("Checking database for existing teams and users before seeding...");
                        System.out.println("Current user count in 'users' table: " + userRepository.count());

                        if (userRepository.count() == 0) {
                                UserEntity defaultUser = UserEntity.builder()
                                                .email("user@coachpad.com")
                                                .password(passwordEncoder.encode("password123"))
                                                .build();
                                userRepository.save(defaultUser);
                                System.out.println("Seeded: Default User (user@coachpad.com / password123)");
                        }


                        // --- FRANCE ---
                        if (!teamRepository.existsByName("France")) {
                                teamRepository.save(createFranceSenior());
                                System.out.println("Seeded: France Senior");
                        }

                        if (!teamRepository.existsByName("France Espoirs")) {
                                teamRepository.save(createFranceEspoirs());
                                System.out.println("Seeded: France Espoirs (U21)");
                        }

                        if (!teamRepository.existsByName("France U19")) {
                                teamRepository.save(createFranceU19());
                                System.out.println("Seeded: France U19");
                        }

                        // --- TUNISIA ---
                        if (!teamRepository.existsByName("Tunisia")) {
                                teamRepository.save(createTunisiaSenior());
                                System.out.println("Seeded: Tunisia (Aagles of Carthage)");
                        }

                        if (!teamRepository.existsByName("Tunisia U23")) {
                                teamRepository.save(createTunisiaU23());
                                System.out.println("Seeded: Tunisia U23");
                        }

                        if (!teamRepository.existsByName("Tunisia U20")) {
                                teamRepository.save(createTunisiaU20());
                                System.out.println("Seeded: Tunisia U20");
                        }

                        System.out.println("Database seeding check completed.");
                };
        }

        private TeamEntity createFranceSenior() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder()
                                .primaryHex("#21304D").secondaryHex("#FFFFFF").trimHex("#ED2939").build();
                TeamDesignEntity design = TeamDesignEntity.builder()
                                .style(WidgetAppearance.CIRCLE).jerseyDesign(JerseyDesign.SOLID).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/4/4e/France_national_football_team_crest.svg/1200px-France_national_football_team_crest.svg.png")
                                .usePlayerPhotos(true).build();
                TeamEntity team = TeamEntity.builder()
                                .name("France").nickname("Les Bleus").ageCategory("SENIOR").design(design)
                                .build();
                colors.setDesign(design);
                design.setTeam(team);

                // Staff Technique
                team.addCoach(createCoach("Didier", "Deschamps", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.TACTICAL_FLEXIBILITY,
                                "https://b.fssta.com/uploads/application/soccer/headshots/2711.png"));
                team.addCoach(createCoach("Guy", "Stéphan", CoachRole.ASSISTANT_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.BALANCED, ""));

                // Gardiens
                team.addPlayer(createPlayer("Mike", "Maignan", 1, "GK", "France", "1995-07-03",
                                "https://b.fssta.com/uploads/application/soccer/headshots/39257.png"));
                team.addPlayer(createPlayer("Brice", "Samba", 16, "GK", "France", "1994-04-25",
                                "https://b.fssta.com/uploads/application/soccer/headshots/28448.png"));
                team.addPlayer(createPlayer("Alphonse", "Areola", 23, "GK", "France", "1993-02-27",
                                "https://b.fssta.com/uploads/application/soccer/headshots/2662.png"));

                // Défenseurs
                team.addPlayer(createPlayer("Jules", "Koundé", 5, "RB", "France", "1998-11-12",
                                "https://b.fssta.com/uploads/application/soccer/headshots/54261.png"));
                team.addPlayer(createPlayer("William", "Saliba", 17, "CB", "France", "2001-03-24",
                                "https://b.fssta.com/uploads/application/soccer/headshots/66734.png"));
                team.addPlayer(createPlayer("Dayot", "Upamecano", 4, "CB", "France", "1998-10-27",
                                "https://b.fssta.com/uploads/application/soccer/headshots/46537.png"));
                team.addPlayer(createPlayer("Theo", "Hernández", 22, "LB", "France", "1997-10-06",
                                "https://b.fssta.com/uploads/application/soccer/headshots/45408.png"));
                team.addPlayer(createPlayer("Ibrahima", "Konaté", 24, "CB", "France", "1999-05-25",
                                "https://b.fssta.com/uploads/application/soccer/headshots/51608.png"));
                team.addPlayer(createPlayer("Jonathan", "Clauss", 21, "RB", "France", "1992-09-25",
                                "https://b.fssta.com/uploads/application/soccer/headshots/74921.png"));
                team.addPlayer(createPlayer("Benjamin", "Pavard", 2, "CB", "France", "1996-03-28",
                                "https://b.fssta.com/uploads/application/soccer/headshots/40237.png"));

                // Milieux
                team.addPlayer(createPlayer("N'Golo", "Kanté", 13, "CDM", "France", "1991-03-29",
                                "https://b.fssta.com/uploads/application/soccer/headshots/31442.png"));
                team.addPlayer(createPlayer("Aurélien", "Tchouaméni", 8, "CM", "France", "2000-01-27",
                                "https://b.fssta.com/uploads/application/soccer/headshots/58145.png"));
                team.addPlayer(createPlayer("Eduardo", "Camavinga", 6, "CM", "France", "2002-11-10",
                                "https://b.fssta.com/uploads/application/soccer/headshots/69367.png"));
                team.addPlayer(createPlayer("Warren", "Zaïre-Emery", 18, "CM", "France", "2006-03-08",
                                "https://b.fssta.com/uploads/application/soccer/headshots/106915.png"));
                team.addPlayer(createPlayer("Adrien", "Rabiot", 14, "CM", "France", "1995-04-03",
                                "https://b.fssta.com/uploads/application/soccer/headshots/2798.png"));
                team.addPlayer(createPlayer("Youssouf", "Fofana", 19, "CM", "France", "1999-01-10",
                                "https://b.fssta.com/uploads/application/soccer/headshots/60114.png"));

                // Attaquants
                team.addPlayer(createPlayer("Kylian", "Mbappé", 10, "LW", "France", "1998-12-20",
                                "https://b.fssta.com/uploads/application/soccer/headshots/40670.png"));
                team.addPlayer(createPlayer("Ousmane", "Dembélé", 11, "RW", "France", "1997-05-15",
                                "https://b.fssta.com/uploads/application/soccer/headshots/39732.png"));
                team.addPlayer(createPlayer("Bradley", "Barcola", 25, "LW", "France", "2002-09-02",
                                "https://b.fssta.com/uploads/application/soccer/headshots/95034.png"));
                team.addPlayer(createPlayer("Marcus", "Thuram", 15, "ST", "France", "1997-08-06",
                                "https://b.fssta.com/uploads/application/soccer/headshots/40141.png"));
                team.addPlayer(createPlayer("Randal", "Kolo Muani", 12, "ST", "France", "1998-12-05",
                                "https://b.fssta.com/uploads/application/soccer/headshots/50949.png"));
                team.addPlayer(createPlayer("Michael", "Olise", 20, "RW", "France", "2001-12-12",
                                "https://b.fssta.com/uploads/application/soccer/headshots/74643.png"));

                return team;
        }

        private TeamEntity createFranceEspoirs() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#21304D").secondaryHex("#FFFFFF")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.SOLID).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/4/4e/France_national_football_team_crest.svg/1200px-France_national_football_team_crest.svg.png")
                                .usePlayerPhotos(true).build();
                TeamEntity team = TeamEntity.builder().name("France Espoirs").nickname("Les Espoirs")
                                .ageCategory("U21")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("Gérald", "Baticle", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT, ""));
                team.addPlayer(createPlayer("Guillaume", "Restes", 1, "GK", "France", "2005-03-11", ""));
                team.addPlayer(createPlayer("Castello", "Lukeba", 4, "CB", "France", "2002-12-17", ""));
                team.addPlayer(createPlayer("Désiré", "Doué", 10, "CAM", "France", "2005-06-03", ""));
                team.addPlayer(createPlayer("Mathys", "Tel", 9, "ST", "France", "2005-04-27", ""));

                return team;
        }

        private TeamEntity createFranceU19() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#21304D").secondaryHex("#FFFFFF")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.SOLID).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/4/4e/France_national_football_team_crest.svg/1200px-France_national_football_team_crest.svg.png")
                                .usePlayerPhotos(false).build();
                TeamEntity team = TeamEntity.builder().name("France U19").nickname("Bleuets")
                                .ageCategory("U19")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("Bernard", "Diomède", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT, ""));
                team.addPlayer(createPlayer("Eli", "Kroupi Jr", 10, "ST", "France", "2006-06-23", ""));

                return team;
        }

        private TeamEntity createTunisiaSenior() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#E70013").secondaryHex("#FFFFFF")
                                .trimHex("#FFFFFF").build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.GEOMETRIC).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/c/cd/Tunisian_Football_Federation_Logo.svg/1200px-Tunisian_Football_Federation_Logo.svg.png")
                                .usePlayerPhotos(true).build();
                TeamEntity team = TeamEntity.builder().name("Tunisia").nickname("Les Aigles de Carthage").ageCategory("SENIOR")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                // Staff Technique
                team.addCoach(createCoach("Sabri", "Lamouchi", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.TACTICAL_FLEXIBILITY, ""));
                team.addCoach(createCoach("Kais", "Yaâkoubi", CoachRole.ASSISTANT_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.BALANCED, ""));

                // Gardiens
                team.addPlayer(createPlayer("Aymen", "Dahmen", 16, "GK", "Tunisia", "1997-01-28", ""));
                team.addPlayer(createPlayer("Bechir", "Ben Saïd", 1, "GK", "Tunisia", "1992-11-29", ""));
                team.addPlayer(createPlayer("Amanallah", "Memmiche", 22, "GK", "Tunisia", "2004-04-20", ""));

                // Défenseurs
                team.addPlayer(createPlayer("Montassar", "Talbi", 3, "CB", "Tunisia", "1998-05-26", ""));
                team.addPlayer(createPlayer("Yassine", "Meriah", 4, "CB", "Tunisia", "1993-07-02", ""));
                team.addPlayer(createPlayer("Dylan", "Bronn", 6, "CB", "Tunisia", "1995-06-19", ""));
                team.addPlayer(createPlayer("Ali", "Abdi", 2, "LB", "Tunisia", "1993-12-20", ""));
                team.addPlayer(createPlayer("Yan", "Valery", 20, "RB", "Tunisia", "1999-02-22", ""));
                team.addPlayer(createPlayer("Alaa", "Ghram", 5, "CB", "Tunisia", "2001-07-24", ""));
                team.addPlayer(createPlayer("Ali", "Maâloul", 12, "LB", "Tunisia", "1990-01-01", ""));

                // Milieux
                team.addPlayer(createPlayer("Ellyes", "Skhiri", 17, "CDM", "Tunisia", "1995-05-10", ""));
                team.addPlayer(createPlayer("Aissa", "Laïdouni", 14, "CM", "Tunisia", "1996-12-13", ""));
                team.addPlayer(createPlayer("Mohamed", "Ali Ben Romdhane", 10, "CM", "Tunisia", "1999-09-06", ""));
                team.addPlayer(createPlayer("Hannibal", "Mejbri", 8, "CAM", "Tunisia", "2003-01-21", ""));
                team.addPlayer(createPlayer("Hamza", "Rafia", 11, "CAM", "Tunisia", "1999-04-02", ""));
                team.addPlayer(createPlayer("Anis", "Ben Slimane", 21, "CM", "Tunisia", "2001-03-16", ""));

                // Attaquants
                team.addPlayer(createPlayer("Youssef", "Msakni", 7, "LW", "Tunisia", "1990-10-28", ""));
                team.addPlayer(createPlayer("Elias", "Achouri", 24, "LW", "Tunisia", "1999-02-10", ""));
                team.addPlayer(createPlayer("Seifeddine", "Jaziri", 19, "ST", "Tunisia", "1993-02-11", ""));
                team.addPlayer(createPlayer("Sayfallah", "Ltaief", 13, "RW", "Tunisia", "2000-04-22", ""));
                team.addPlayer(createPlayer("Elias", "Saad", 18, "ST", "Tunisia", "1999-12-27", ""));

                return team;
        }

        private TeamEntity createTunisiaU23() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#E70013").secondaryHex("#FFFFFF")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.GEOMETRIC).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/c/cd/Tunisian_Football_Federation_Logo.svg/1200px-Tunisian_Football_Federation_Logo.svg.png")
                                .usePlayerPhotos(false).build();
                TeamEntity team = TeamEntity.builder().name("Tunisia U23").nickname("Aiglons").ageCategory("U23")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("Maher", "Kanzari", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT, ""));
                team.addPlayer(createPlayer("Chiheb", "Labidi", 10, "CAM", "Tunisia", "2001-06-01", ""));

                return team;
        }

        private TeamEntity createTunisiaU20() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#E70013").secondaryHex("#FFFFFF")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.GEOMETRIC).colors(colors)
                                .logoFilePath(
                                                "https://upload.wikimedia.org/wikipedia/en/thumb/c/cd/Tunisian_Football_Federation_Logo.svg/1200px-Tunisian_Football_Federation_Logo.svg.png")
                                .usePlayerPhotos(false).build();
                TeamEntity team = TeamEntity.builder().name("Tunisia U20").nickname("Jeunes Aigles").ageCategory("U20")
                                .design(design)
                                .build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("Anis", "Boussaidi", CoachRole.HEAD_COACH, LicenseLevel.UEFA_A,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT, ""));
                team.addPlayer(createPlayer("Youssef", "Snana", 9, "ST", "Tunisia", "2004-03-24", ""));

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
