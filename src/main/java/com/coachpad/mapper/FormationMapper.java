package com.coachpad.mapper;

import com.coachpad.dto.FormationDTO;
import com.coachpad.model.FormationModel;
import com.coachpad.persistence.entity.FormationEntity;
import org.springframework.stereotype.Component;

@Component
public class FormationMapper {

    /** 🔹 Convertit une entité (BDD) en modèle (métier) */
    public static FormationModel toModel(FormationEntity entity) {
        if (entity == null) return null;

        return FormationModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .orderedPositions(entity.getOrderedPositions())
                .formationFormat(entity.getFormationFormat())
                .valid(entity.isValid())
                .build();
    }

    /** 🔹 Convertit un modèle (métier) en entité (BDD) */
    public static FormationEntity toEntity(FormationModel model) {
        if (model == null) return null;

        return FormationEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .orderedPositions(model.getOrderedPositions())
                .build();
    }

    /** 🔹 Convertit un DTO (frontend) en entité (BDD) */
    public FormationEntity toEntity(FormationDTO dto) {
        if (dto == null) return null;

        return FormationEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .orderedPositions(dto.getOrderedPositions())
                .build();
    }

    /** 🔹 Convertit une entité (BDD) en DTO (pour le frontend) */
    public FormationDTO toDto(FormationEntity entity) {
        if (entity == null) return null;

        return FormationDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .orderedPositions(entity.getOrderedPositions())
                .formationFormat(entity.getFormationFormat())
                .valid(entity.isValid())
                .build();
    }
}
