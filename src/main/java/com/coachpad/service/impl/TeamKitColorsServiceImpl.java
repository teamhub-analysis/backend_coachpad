package com.coachpad.service.impl;

import com.coachpad.persistence.entity.TeamKitColorsEntity;
import com.coachpad.persistence.repository.TeamKitColorsRepository;
import com.coachpad.service.TeamKitColorsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamKitColorsServiceImpl implements TeamKitColorsService {

    private final TeamKitColorsRepository colorsRepository;

    @Override
    public TeamKitColorsEntity createColors(TeamKitColorsEntity colors) {
        return colorsRepository.save(colors);
    }

    @Override
    public TeamKitColorsEntity updateColors(Long id, TeamKitColorsEntity updatedColors) {
        TeamKitColorsEntity existing = colorsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Couleurs non trouvées avec id : " + id));

        existing.setPrimaryHex(updatedColors.getPrimaryHex());
        existing.setSecondaryHex(updatedColors.getSecondaryHex());
        existing.setTrimHex(updatedColors.getTrimHex());

        return colorsRepository.save(existing);
    }

    @Override
    public Optional<TeamKitColorsEntity> getColorsById(Long id) {
        return colorsRepository.findById(id);
    }

    @Override
    public List<TeamKitColorsEntity> getAllColors() {
        return colorsRepository.findAll();
    }

    @Override
    public void deleteColors(Long id) {
        if (!colorsRepository.existsById(id)) {
            throw new EntityNotFoundException("Couleurs non trouvées avec id : " + id);
        }
        colorsRepository.deleteById(id);
    }
}
