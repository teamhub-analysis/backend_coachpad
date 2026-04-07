package com.coachpad.mapper;

import com.coachpad.dto.SquadGroupDTO;
import com.coachpad.persistence.entity.SquadGroupEntity;
import com.coachpad.persistence.entity.PlayerEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", imports = { PlayerEntity.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SquadGroupMapper {

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "playerIds", expression = "java(entity.getPlayers() != null ? entity.getPlayers().stream().map(PlayerEntity::getId).toList() : null)")
    SquadGroupDTO toDTO(SquadGroupEntity entity);

    @Mapping(target = "team", ignore = true)
    @Mapping(target = "players", ignore = true)
    SquadGroupEntity toEntity(SquadGroupDTO dto);

    List<SquadGroupDTO> toDTOList(List<SquadGroupEntity> entities);

    List<SquadGroupEntity> toEntityList(List<SquadGroupDTO> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "players", ignore = true)
    void updateEntityFromDTO(SquadGroupDTO dto, @MappingTarget SquadGroupEntity entity);
}
