package com.coachpad.persistence.adapter;

import com.coachpad.mapper.CoachMapper;
import com.coachpad.model.CoachModel;
import com.coachpad.persistence.entity.CoachEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoachAdapter {

    private final CoachMapper mapper;

    public CoachModel toModel(CoachEntity entity) {
        return mapper.toModel(entity);
    }

    public CoachEntity toEntity(CoachModel model) {
        return mapper.toEntity(model);
    }

    public List<CoachModel> toModelList(List<CoachEntity> entities) {
        return entities.stream().map(mapper::toModel).toList();
    }

    public List<CoachEntity> toEntityList(List<CoachModel> models) {
        return models.stream().map(mapper::toEntity).toList();
    }
}
