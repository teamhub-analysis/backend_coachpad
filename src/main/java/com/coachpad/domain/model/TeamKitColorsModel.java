package com.coachpad.domain.model;

import com.coachpad.domain.model.util.ColorUtils;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class TeamKitColorsModel {

    private Long id;

    private String primaryHex;

    private String secondaryHex;

    private String trimHex;

    public boolean hasGoodContrast() {
        return ColorUtils.hasGoodContrast(primaryHex, secondaryHex);
    }

    public String getPreview() {
        return String.format(
                "Primary: %s | Secondary: %s | Trim: %s | Contrast OK: %s",
                primaryHex,
                secondaryHex,
                trimHex,
                hasGoodContrast() ? "OK" : "WARN"
        );
    }
}
