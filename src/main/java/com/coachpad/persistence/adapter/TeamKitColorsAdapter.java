// src/main/java/com/coachpad/service/adapter/TeamKitColorsAdapter.java
package com.coachpad.persistence.adapter;

import com.coachpad.dto.TeamKitColorsDTO;
import com.coachpad.mapper.TeamKitColorsMapper;
import com.coachpad.persistence.entity.TeamKitColorsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamKitColorsAdapter {

    private final TeamKitColorsMapper mapper;

    public TeamKitColorsDTO toDto(TeamKitColorsEntity entity) {
        return mapper.toDto(entity);
    }

    public TeamKitColorsEntity toEntity(TeamKitColorsDTO dto) {
        TeamKitColorsEntity entity = mapper.toEntity(dto);
        validateColors(dto);
        return entity;
    }

    public void updateEntity(TeamKitColorsDTO dto, TeamKitColorsEntity entity) {
        validateColors(dto);
        mapper.updateEntityFromDto(dto, entity);
    }

    private void validateColors(TeamKitColorsDTO dto) {
        if (dto.getPrimaryHex() != null && !TeamKitColorsEntity.isValidHexColor(dto.getPrimaryHex())) {
            throw new IllegalArgumentException("Invalid primary hex color: " + dto.getPrimaryHex());
        }
        if (dto.getSecondaryHex() != null && !TeamKitColorsEntity.isValidHexColor(dto.getSecondaryHex())) {
            throw new IllegalArgumentException("Invalid secondary hex color: " + dto.getSecondaryHex());
        }
        if (dto.getTrimHex() != null && !TeamKitColorsEntity.isValidHexColor(dto.getTrimHex())) {
            throw new IllegalArgumentException("Invalid trim hex color: " + dto.getTrimHex());
        }
    }
}