package com.coachpad.persistence.adapter;

import com.coachpad.model.CoachModel;
import com.coachpad.persistence.entity.CoachEntity;
import org.springframework.stereotype.Component;

@Component
public class CoachAdapter {

    // 🔁 Convertir une Entity vers un Model
    public CoachModel toModel(CoachEntity entity) {
        if (entity == null) return null;

        return CoachModel.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(entity.getDisplayName())
                .nationality(entity.getNationality())
                .photoUrl(entity.getPhotoUrl())
                .licenseLevel(entity.getLicenseLevel())
                .contractEndDate(entity.getContractEndDate())
                .coachingPhilosophy(entity.getCoachingPhilosophy())
                .coachingPhilosophyDescription(entity.getCoachingPhilosophyDescription())
                .build();
    }

    // 🔁 Convertir un Model vers une Entity
    public CoachEntity toEntity(CoachModel model) {
        if (model == null) return null;

        return CoachEntity.builder()
                .id(model.getId())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .fullName(model.getFullName())
                .nationality(model.getNationality())
                .photoUrl(model.getPhotoUrl())
                .licenseLevel(model.getLicenseLevel())
                .contractEndDate(model.getContractEndDate())
                .coachingPhilosophy(model.getCoachingPhilosophy())
                .coachingPhilosophyDescription(model.getCoachingPhilosophyDescription())
                .build();
    }
}
