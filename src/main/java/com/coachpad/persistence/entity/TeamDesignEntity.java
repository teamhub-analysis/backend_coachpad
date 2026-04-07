package com.coachpad.persistence.entity;
import com.coachpad.persistence.Enum.JerseyDesign;
import com.coachpad.persistence.Enum.WidgetAppearance;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_designs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "team")
@EqualsAndHashCode(of = "id")
public class TeamDesignEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "style", length = 50)
    private WidgetAppearance style;

    @Column(name = "logo_file_path")
    private String logoFilePath;

    @Column(name = "logo_icon_name", length = 100)
    private String logoIconName;

    @Column(name = "use_player_photos")
    private Boolean usePlayerPhotos = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "jersey_design", length = 50)
    private JerseyDesign jerseyDesign;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "colors_id", nullable = false)
    private TeamKitColorsEntity colors;

    @Enumerated(EnumType.STRING)
    @Column(name = "gk_style", length = 50)
    private WidgetAppearance gkStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "gk_jersey_design", length = 50)
    private JerseyDesign gkJerseyDesign;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gk_colors_id")
    private TeamKitColorsEntity gkColors;

    @OneToOne(mappedBy = "design", fetch = FetchType.LAZY)
    private TeamEntity team;

    /**
     * Vérifie si le design a un logo personnalisé
     */
    public boolean hasCustomLogo() {
        return logoFilePath != null && !logoFilePath.trim().isEmpty();
    }

    /**
     * Vérifie si le design utilise une icône Flutter
     */
    public boolean hasIconLogo() {
        return logoIconName != null && !logoIconName.trim().isEmpty();
    }

    /**
     * Retourne le chemin complet du logo ou null
     */
    public String getFullLogoPath() {
        if (hasCustomLogo()) {
            return logoFilePath;
        }
        return null;
    }

    /**
     * Valide que le design est complet
     */
    public boolean isValid() {
        return colors != null && 
               jerseyDesign != null && 
               (hasCustomLogo() || hasIconLogo());
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (style == null) {
            style = WidgetAppearance.CIRCLE;
        }
        
        if (jerseyDesign == null) {
            jerseyDesign = JerseyDesign.SOLID;
        }
        
        if (!hasCustomLogo() && !hasIconLogo()) {
            logoIconName = "sports_soccer";
        }
        
        if (usePlayerPhotos == null) {
            usePlayerPhotos = false;
        }

        if (gkStyle == null) {
            gkStyle = WidgetAppearance.MODERN;
        }

        if (gkJerseyDesign == null) {
            gkJerseyDesign = JerseyDesign.PLAIN;
        }

        if (gkColors == null && colors != null) {
            // Par défaut, utiliser les mêmes couleurs que les joueurs pour le gardien
            // (Note: dans une vraie app, on pourrait créer un nouvel objet si besoin)
        }
    }
}