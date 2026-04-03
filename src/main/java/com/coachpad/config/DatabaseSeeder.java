package com.coachpad.config;

import com.coachpad.persistence.Enum.JerseyDesign;
import com.coachpad.persistence.Enum.WidgetAppearance;
import com.coachpad.persistence.entity.PlayerEntity;
import com.coachpad.persistence.entity.TeamDesignEntity;
import com.coachpad.persistence.entity.TeamEntity;
import com.coachpad.persistence.entity.TeamKitColorsEntity;
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
                        teamRepository.deleteAll(); // <-- Clear existing teams
                        // Team 1: Real Madrid
                                TeamKitColorsEntity realColors = TeamKitColorsEntity.builder()
                                                .primaryHex("#FFFFFF")
                                                .secondaryHex("#000000")
                                                .build();

                                TeamDesignEntity realDesign = TeamDesignEntity.builder()
                                                .style(WidgetAppearance.CIRCLE)
                                                .jerseyDesign(JerseyDesign.SOLID)
                                                .colors(realColors)
                                                .logoFilePath("https://upload.wikimedia.org/wikipedia/en/thumb/5/56/Real_Madrid_CF.svg/1200px-Real_Madrid_CF.svg.png")
                                                .usePlayerPhotos(true)
                                                .build();

                                TeamEntity realMadrid = TeamEntity.builder()
                                                .name("Real Madrid")
                                                .nickname("Los Blancos")
                                                .ageCategory("Senior")
                                                .design(realDesign)
                                                .build();

                                // Assign back reference
                                realColors.setDesign(realDesign);
                                realDesign.setTeam(realMadrid);

                                PlayerEntity vini = PlayerEntity.builder()
                                                .firstName("Vinícius")
                                                .lastName("Júnior")
                                                .number(7)
                                                .dateOfBirth(LocalDate.of(2000, 7, 12))
                                                .nationality("Brazil")
                                                .mainPosition("LW")
                                                .photoUrl("https://b.fssta.com/uploads/application/soccer/headshots/40632.png")
                                                .team(realMadrid)
                                                .build();

                                PlayerEntity bellingham = PlayerEntity.builder()
                                                .firstName("Jude")
                                                .lastName("Bellingham")
                                                .number(5)
                                                .dateOfBirth(LocalDate.of(2003, 6, 29))
                                                .nationality("England")
                                                .mainPosition("CM")
                                                .photoUrl("https://b.fssta.com/uploads/application/soccer/headshots/71310.png")
                                                .team(realMadrid)
                                                .build();

                                realMadrid.addPlayer(vini);
                                realMadrid.addPlayer(bellingham);

                                // Team 2: Paris Saint-Germain
                                TeamKitColorsEntity psgColors = TeamKitColorsEntity.builder()
                                                .primaryHex("#004170")
                                                .secondaryHex("#DA291C")
                                                .trimHex("#FFFFFF")
                                                .build();

                                TeamDesignEntity psgDesign = TeamDesignEntity.builder()
                                                .style(WidgetAppearance.CIRCLE)
                                                .jerseyDesign(JerseyDesign.STRIPED_VERTICAL)
                                                .colors(psgColors)
                                                .logoFilePath("https://upload.wikimedia.org/wikipedia/en/thumb/a/a7/Paris_Saint-Germain_F.C..svg/1200px-Paris_Saint-Germain_F.C..svg.png")
                                                .usePlayerPhotos(true)
                                                .build();

                                TeamEntity psg = TeamEntity.builder()
                                                .name("Paris Saint-Germain")
                                                .nickname("PSG")
                                                .ageCategory("Senior")
                                                .design(psgDesign)
                                                .build();

                                // Assign back reference
                                psgColors.setDesign(psgDesign);
                                psgDesign.setTeam(psg);

                                PlayerEntity marquinhos = PlayerEntity.builder()
                                                .firstName("Marcos")
                                                .lastName("Aoás Corrêa")
                                                .fullName("Marquinhos")
                                                .number(5)
                                                .dateOfBirth(LocalDate.of(1994, 5, 14))
                                                .nationality("Brazil")
                                                .mainPosition("CB")
                                                .photoUrl("https://b.fssta.com/uploads/application/soccer/headshots/17278.png")
                                                .team(psg)
                                                .build();

                                PlayerEntity hakimi = PlayerEntity.builder()
                                                .firstName("Achraf")
                                                .lastName("Hakimi")
                                                .number(2)
                                                .dateOfBirth(LocalDate.of(1998, 11, 4))
                                                .nationality("Morocco")
                                                .mainPosition("RB")
                                                .photoUrl("https://b.fssta.com/uploads/application/soccer/headshots/40602.png")
                                                .team(psg)
                                                .build();

                                psg.addPlayer(marquinhos);
                                psg.addPlayer(hakimi);

                                teamRepository.saveAll(List.of(realMadrid, psg));
                };
        }
}
