// src/main/java/com/coachpad/dto/TeamKitColorsDto.java
package com.coachpad.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamKitColorsDTO {

    private Long id;

    private String primaryHex;   // #RRGGBB
    private String secondaryHex; // #RRGGBB ou null
    private String trimHex;      // #RRGGBB ou null

    // Champ calculé - Ajoutez @Builder.Default pour que le builder le reconnaisse
    @Builder.Default
    private Boolean hasGoodContrast = null;
}