package com.coachpad.infrastructure.persistance.postgresql.entity;

import com.coachpad.domain.model.util.ColorUtils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_colors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "design")
@EqualsAndHashCode(of = "id")
public class TeamKitColorsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primary_hex", nullable = false, length = 7)
    private String primaryHex;

    @Column(name = "secondary_hex", length = 7)
    private String secondaryHex;

    @Column(name = "trim_hex", length = 7)
    private String trimHex;

    @OneToOne(mappedBy = "colors", fetch = FetchType.LAZY)
    private TeamDesignEntity design;

    public boolean hasGoodContrast() {
        return ColorUtils.hasGoodContrast(primaryHex, secondaryHex);
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (primaryHex != null) {
            this.primaryHex = ColorUtils.normalizeHexColor(primaryHex);
            if (!ColorUtils.isValidHexColor(this.primaryHex)) {
                throw new IllegalArgumentException("Invalid primary color: " + primaryHex);
            }
        }
        if (secondaryHex != null) {
            this.secondaryHex = ColorUtils.normalizeHexColor(secondaryHex);
            if (!ColorUtils.isValidHexColor(this.secondaryHex)) {
                throw new IllegalArgumentException("Invalid secondary color: " + secondaryHex);
            }
        }
        if (trimHex != null) {
            this.trimHex = ColorUtils.normalizeHexColor(trimHex);
            if (!ColorUtils.isValidHexColor(this.trimHex)) {
                throw new IllegalArgumentException("Invalid trim color: " + trimHex);
            }
        }
    }
}
