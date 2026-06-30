package com.coachpad.infrastructure.config;

import com.coachpad.domain.model.enums.*;
import com.coachpad.infrastructure.persistance.postgresql.entity.*;
import com.coachpad.infrastructure.persistance.postgresql.repository.TeamJpaRepository;
import com.coachpad.infrastructure.persistance.postgresql.repository.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

        private final PasswordEncoder passwordEncoder;

        @Value("${app.default-user.email}")
        private String defaultUserEmail;

        @Value("${app.default-user.password}")
        private String defaultUserPassword;

        @Bean
        CommandLineRunner initDatabase(TeamJpaRepository TeamJpaRepository, UserJpaRepository UserJpaRepository) {
                return args -> {
                        log.info("Checking database for existing teams and users before seeding...");
                        log.info("Current user count in 'users' table: {}", UserJpaRepository.count());

                        if (UserJpaRepository.count() == 0) {
                                UserEntity defaultUser = UserEntity.builder()
                                                .email(defaultUserEmail)
                                                .password(passwordEncoder.encode(defaultUserPassword))
                                                .build();
                                UserJpaRepository.save(defaultUser);
                                log.info("Seeded: Default User ({})", defaultUserEmail);
                        }


                        // --- FRANCE ---
                        if (!TeamJpaRepository.existsByName("France")) {
                                TeamJpaRepository.save(createFranceSenior());
                                log.info("Seeded: France Senior");
                        }

                        if (!TeamJpaRepository.existsByName("France Espoirs")) {
                                TeamJpaRepository.save(createFranceEspoirs());
                                log.info("Seeded: France Espoirs (U21)");
                        }

                        if (!TeamJpaRepository.existsByName("France U19")) {
                                TeamJpaRepository.save(createFranceU19());
                                log.info("Seeded: France U19");
                        }

                        // --- TUNISIA ---
                        if (!TeamJpaRepository.existsByName("Tunisia")) {
                                TeamJpaRepository.save(createTunisiaSenior());
                                log.info("Seeded: Tunisia (Aagles of Carthage)");
                        }

                        if (!TeamJpaRepository.existsByName("Tunisia U23")) {
                                TeamJpaRepository.save(createTunisiaU23());
                                log.info("Seeded: Tunisia U23");
                        }

                        if (!TeamJpaRepository.existsByName("Tunisia U20")) {
                                TeamJpaRepository.save(createTunisiaU20());
                                log.info("Seeded: Tunisia U20");
                        }

                        log.info("Database seeding check completed.");
                };
        }

        private TeamEntity createFranceSenior() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder()
                                .primaryHex("#21304D").secondaryHex("#FFFFFF").trimHex("#ED2939").build();
                TeamDesignEntity design = TeamDesignEntity.builder()
                                .style(WidgetAppearance.CIRCLE).jerseyDesign(JerseyDesign.SOLID).colors(colors)
                                .logoFilePath("https://flagcdn.com/w320/fr.png")
                                .usePlayerPhotos(true).build();
                TeamEntity team = TeamEntity.builder()
                                .name("France").nickname("Les Bleus").ageCategory("SENIOR").design(design)
                                .build();
                colors.setDesign(design);
                design.setTeam(team);

                // Staff Technique
                team.addCoach(createCoach("Didier", "Deschamps", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.TACTICAL_FLEXIBILITY,
                                "https://r2.thesportsdb.com/images/media/player/cutout/adxgwj1592666238.png"));
                team.addCoach(createCoach("Guy", "StÃƒÂ©phan", CoachRole.ASSISTANT_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.BALANCED, ""));

                // Gardiens
                team.addPlayer(createPlayer("Mike", "Maignan", 1, "GK", "France", "1995-07-03",
                                "https://r2.thesportsdb.com/images/media/player/cutout/sw5ukh1758892671.png"));
                team.addPlayer(createPlayer("Brice", "Samba", 16, "GK", "France", "1994-04-25",
                                "https://r2.thesportsdb.com/images/media/player/cutout/nkfugp1766137864.png"));
                team.addPlayer(createPlayer("Alphonse", "Areola", 23, "GK", "France", "1993-02-27",
                                "https://r2.thesportsdb.com/images/media/player/cutout/istdrx1756985080.png"));

                // DÃƒÂ©fenseurs
                team.addPlayer(createPlayer("Jules", "KoundÃƒÂ©", 5, "RB", "France", "1998-11-12",
                                "https://r2.thesportsdb.com/images/media/player/cutout/qea88i1726509803.png"));
                team.addPlayer(createPlayer("William", "Saliba", 17, "CB", "France", "2001-03-24",
                                "https://r2.thesportsdb.com/images/media/player/cutout/czasy21769331889.png"));
                team.addPlayer(createPlayer("Dayot", "Upamecano", 4, "CB", "France", "1998-10-27",
                                "https://r2.thesportsdb.com/images/media/player/cutout/a1hyfj1756416177.png"));
                team.addPlayer(createPlayer("Theo", "HernÃƒÂ¡ndez", 22, "LB", "France", "1997-10-06",
                                "https://r2.thesportsdb.com/images/media/player/cutout/4d3g7j1675234242.png"));
                team.addPlayer(createPlayer("Ibrahima", "KonatÃƒÂ©", 24, "CB", "France", "1999-05-25",
                                "https://r2.thesportsdb.com/images/media/player/cutout/izock91757088476.png"));
                team.addPlayer(createPlayer("Jonathan", "Clauss", 21, "RB", "France", "1992-09-25",
                                "https://r2.thesportsdb.com/images/media/player/cutout/lmb4f41766319695.png"));
                team.addPlayer(createPlayer("Benjamin", "Pavard", 2, "CB", "France", "1996-03-28",
                                "https://r2.thesportsdb.com/images/media/player/cutout/d9p3381766153512.png"));

                // Milieux
                team.addPlayer(createPlayer("N'Golo", "KantÃƒÂ©", 13, "CDM", "France", "1991-03-29",
                                "https://r2.thesportsdb.com/images/media/player/cutout/ld6low1719039995.png"));
                team.addPlayer(createPlayer("AurÃƒÂ©lien", "TchouamÃƒÂ©ni", 8, "CM", "France", "2000-01-27",
                                "https://r2.thesportsdb.com/images/media/player/cutout/4o417k1733653668.png"));
                team.addPlayer(createPlayer("Eduardo", "Camavinga", 6, "CM", "France", "2002-11-10",
                                "https://r2.thesportsdb.com/images/media/player/cutout/viijpx1733653403.png"));
                team.addPlayer(createPlayer("Warren", "ZaÃƒÂ¯re-Emery", 18, "CM", "France", "2006-03-08",
                                "https://r2.thesportsdb.com/images/media/player/cutout/fjxbac1766335583.png"));
                team.addPlayer(createPlayer("Adrien", "Rabiot", 14, "CM", "France", "1995-04-03",
                                "https://r2.thesportsdb.com/images/media/player/cutout/m2upnx1758893486.png"));
                team.addPlayer(createPlayer("Youssouf", "Fofana", 19, "CM", "France", "1999-01-10",
                                "https://r2.thesportsdb.com/images/media/player/cutout/3npg7a1758892447.png"));

                // Attaquants
                team.addPlayer(createPlayer("Kylian", "MbappÃƒÂ©", 10, "LW", "France", "1998-12-20",
                                "https://r2.thesportsdb.com/images/media/player/cutout/h9u9vz1733653583.png"));
                team.addPlayer(createPlayer("Ousmane", "DembÃƒÂ©lÃƒÂ©", 11, "RW", "France", "1997-05-15",
                                "https://r2.thesportsdb.com/images/media/player/cutout/pstgy21766335175.png"));
                team.addPlayer(createPlayer("Bradley", "Barcola", 25, "LW", "France", "2002-09-02",
                                "https://r2.thesportsdb.com/images/media/player/cutout/l2v71f1766334537.png"));
                team.addPlayer(createPlayer("Marcus", "Thuram", 15, "ST", "France", "1997-08-06",
                                "https://r2.thesportsdb.com/images/media/player/cutout/aykui01759408989.png"));
                team.addPlayer(createPlayer("Randal", "Kolo Muani", 12, "ST", "France", "1998-12-05",
                                "https://r2.thesportsdb.com/images/media/player/cutout/h89zyk1768679563.png"));
                team.addPlayer(createPlayer("Michael", "Olise", 20, "RW", "France", "2001-12-12",
                                "https://r2.thesportsdb.com/images/media/player/cutout/r4vx6b1756408807.png"));

                return team;
        }

        private TeamEntity createFranceEspoirs() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#21304D").secondaryHex("#FFFFFF")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.SOLID).colors(colors)
                                .logoFilePath("https://flagcdn.com/w320/fr.png")
                                .usePlayerPhotos(true).build();
                TeamEntity team = TeamEntity.builder().name("France Espoirs").nickname("Les Espoirs")
                                .ageCategory("U21")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("GÃƒÂ©rald", "Baticle", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT, ""));
                team.addPlayer(createPlayer("Guillaume", "Restes", 1, "GK", "France", "2005-03-11",
                                "https://r2.thesportsdb.com/images/media/player/cutout/3hgd7j1765967162.png"));
                team.addPlayer(createPlayer("Castello", "Lukeba", 4, "CB", "France", "2002-12-17",
                                "https://r2.thesportsdb.com/images/media/player/cutout/3wfwi21763561231.png"));
                team.addPlayer(createPlayer("DÃƒÂ©sirÃƒÂ©", "DouÃƒÂ©", 10, "CAM", "France", "2005-06-03",
                                "https://r2.thesportsdb.com/images/media/player/cutout/5m0p4g1766335194.png"));
                team.addPlayer(createPlayer("Mathys", "Tel", 9, "ST", "France", "2005-04-27",
                                "https://r2.thesportsdb.com/images/media/player/cutout/4e915x1757016205.png"));

                return team;
        }

        private TeamEntity createFranceU19() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#21304D").secondaryHex("#FFFFFF")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.SOLID).colors(colors)
                                .logoFilePath("https://flagcdn.com/w320/fr.png")
                                .usePlayerPhotos(false).build();
                TeamEntity team = TeamEntity.builder().name("France U19").nickname("Bleuets")
                                .ageCategory("U19")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                team.addCoach(createCoach("Bernard", "DiomÃƒÂ¨de", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.YOUTH_DEVELOPMENT, ""));
                team.addPlayer(createPlayer("Eli", "Kroupi Jr", 10, "ST", "France", "2006-06-23", ""));

                return team;
        }

        private TeamEntity createTunisiaSenior() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#E70013").secondaryHex("#FFFFFF")
                                .trimHex("#FFFFFF").build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.GEOMETRIC).colors(colors)
                                .logoFilePath("https://flagcdn.com/w320/tn.png")
                                .usePlayerPhotos(true).build();
                TeamEntity team = TeamEntity.builder().name("Tunisia").nickname("Les Aigles de Carthage").ageCategory("SENIOR")
                                .design(design).build();
                colors.setDesign(design);
                design.setTeam(team);

                // Staff Technique
                team.addCoach(createCoach("Sabri", "Lamouchi", CoachRole.HEAD_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.TACTICAL_FLEXIBILITY, ""));
                team.addCoach(createCoach("Kais", "YaÃƒÂ¢koubi", CoachRole.ASSISTANT_COACH, LicenseLevel.UEFA_PRO,
                                CoachingPhilosophy.BALANCED, ""));

                // Gardiens
                team.addPlayer(createPlayer("Aymen", "Dahmen", 16, "GK", "Tunisia", "1997-01-28",
                                "https://r2.thesportsdb.com/images/media/player/cutout/x3irao1669130503.png"));
                team.addPlayer(createPlayer("Bechir", "Ben SaÃƒÂ¯d", 1, "GK", "Tunisia", "1992-11-29",
                                "https://r2.thesportsdb.com/images/media/player/cutout/rghukf1668807895.png"));
                team.addPlayer(createPlayer("Amanallah", "Memmiche", 22, "GK", "Tunisia", "2004-04-20", ""));

                // DÃƒÂ©fenseurs
                team.addPlayer(createPlayer("Montassar", "Talbi", 3, "CB", "Tunisia", "1998-05-26",
                                "https://r2.thesportsdb.com/images/media/player/cutout/197tnc1765969875.png"));
                team.addPlayer(createPlayer("Yassine", "Meriah", 4, "CB", "Tunisia", "1993-07-02",
                                "https://r2.thesportsdb.com/images/media/player/cutout/fcq00t1750491512.png"));
                team.addPlayer(createPlayer("Dylan", "Bronn", 6, "CB", "Tunisia", "1995-06-19",
                                "https://r2.thesportsdb.com/images/media/player/cutout/u75m3r1707691637.png"));
                team.addPlayer(createPlayer("Ali", "Abdi", 2, "LB", "Tunisia", "1993-12-20",
                                "https://r2.thesportsdb.com/images/media/player/cutout/3v1p3u1766319540.png"));
                team.addPlayer(createPlayer("Yan", "Valery", 20, "RB", "Tunisia", "1999-02-22",
                                "https://r2.thesportsdb.com/images/media/player/cutout/lpxr861761336766.png"));
                team.addPlayer(createPlayer("Alaa", "Ghram", 5, "CB", "Tunisia", "2001-07-24", ""));
                team.addPlayer(createPlayer("Ali", "MaÃƒÂ¢loul", 12, "LB", "Tunisia", "1990-01-01",
                                "https://r2.thesportsdb.com/images/media/player/cutout/zq266q1668856496.png"));

                // Milieux
                team.addPlayer(createPlayer("Ellyes", "Skhiri", 17, "CDM", "Tunisia", "1995-05-10",
                                "https://r2.thesportsdb.com/images/media/player/cutout/zhs8151762287564.png"));
                team.addPlayer(createPlayer("Aissa", "LaÃƒÂ¯douni", 14, "CM", "Tunisia", "1996-12-13",
                                "https://r2.thesportsdb.com/images/media/player/cutout/u1w0cw1678366479.png"));
                team.addPlayer(createPlayer("Mohamed", "Ali Ben Romdhane", 10, "CM", "Tunisia", "1999-09-06",
                                "https://r2.thesportsdb.com/images/media/player/cutout/5ve60c1750398340.png"));
                team.addPlayer(createPlayer("Hannibal", "Mejbri", 8, "CAM", "Tunisia", "2003-01-21",
                                "https://r2.thesportsdb.com/images/media/player/cutout/aupcea1757174643.png"));
                team.addPlayer(createPlayer("Hamza", "Rafia", 11, "CAM", "Tunisia", "1999-04-02",
                                "https://r2.thesportsdb.com/images/media/player/cutout/qpg5s01764339523.png"));
                team.addPlayer(createPlayer("Anis", "Ben Slimane", 21, "CM", "Tunisia", "2001-03-16",
                                "https://r2.thesportsdb.com/images/media/player/cutout/mwg4h31761737613.png"));

                // Attaquants
                team.addPlayer(createPlayer("Youssef", "Msakni", 7, "LW", "Tunisia", "1990-10-28",
                                "https://r2.thesportsdb.com/images/media/player/cutout/imx6h61668861739.png"));
                team.addPlayer(createPlayer("Elias", "Achouri", 24, "LW", "Tunisia", "1999-02-10",
                                "https://r2.thesportsdb.com/images/media/player/cutout/ytwbcx1742471881.png"));
                team.addPlayer(createPlayer("Seifeddine", "Jaziri", 19, "ST", "Tunisia", "1993-02-11",
                                "https://r2.thesportsdb.com/images/media/player/cutout/8aksd91668861917.png"));
                team.addPlayer(createPlayer("Sayfallah", "Ltaief", 13, "RW", "Tunisia", "2000-04-22",
                                "https://r2.thesportsdb.com/images/media/player/cutout/9zrxw01772817278.png"));
                team.addPlayer(createPlayer("Elias", "Saad", 18, "ST", "Tunisia", "1999-12-27",
                                "https://r2.thesportsdb.com/images/media/player/cutout/dqgj1b1773070951.png"));

                return team;
        }

        private TeamEntity createTunisiaU23() {
                TeamKitColorsEntity colors = TeamKitColorsEntity.builder().primaryHex("#E70013").secondaryHex("#FFFFFF")
                                .build();
                TeamDesignEntity design = TeamDesignEntity.builder().style(WidgetAppearance.CIRCLE)
                                .jerseyDesign(JerseyDesign.GEOMETRIC).colors(colors)
                                .logoFilePath("https://flagcdn.com/w320/tn.png")
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
                                .logoFilePath("https://flagcdn.com/w320/tn.png")
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
