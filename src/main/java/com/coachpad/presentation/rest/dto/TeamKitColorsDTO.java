package com.coachpad.presentation.rest.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamKitColorsDTO {

    private Long id;

    private String primaryHex;
    private String secondaryHex;
    private String trimHex;

    @Builder.Default
    private Boolean hasGoodContrast = null;
}
