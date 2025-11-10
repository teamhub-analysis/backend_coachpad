package com.coachpad.persistence.Enum;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum FootballPosition {

    // Gardien
    GK(PositionCategory.GOALKEEPER),

    // Défenseurs
    CB(PositionCategory.DEFENDER),
    LCB(PositionCategory.DEFENDER),
    RCB(PositionCategory.DEFENDER),
    RB(PositionCategory.DEFENDER),
    LB(PositionCategory.DEFENDER),
    RWB(PositionCategory.DEFENDER),
    LWB(PositionCategory.DEFENDER),
    SW(PositionCategory.DEFENDER),
    CWB(PositionCategory.DEFENDER),

    // Milieux
    CDM(PositionCategory.MIDFIELDER),
    LDM(PositionCategory.MIDFIELDER),
    RDM(PositionCategory.MIDFIELDER),
    CM(PositionCategory.MIDFIELDER),
    LCM(PositionCategory.MIDFIELDER),
    RCM(PositionCategory.MIDFIELDER),
    CAM(PositionCategory.MIDFIELDER),
    LAM(PositionCategory.MIDFIELDER),
    RAM(PositionCategory.MIDFIELDER),
    RM(PositionCategory.MIDFIELDER),
    LM(PositionCategory.MIDFIELDER),

    // Attaquants
    RW(PositionCategory.FORWARD),
    LW(PositionCategory.FORWARD),
    ST(PositionCategory.FORWARD),
    LS(PositionCategory.FORWARD),
    RS(PositionCategory.FORWARD),
    CF(PositionCategory.FORWARD),
    SS(PositionCategory.FORWARD);

    private final PositionCategory category;

    // ✅ Cache pour performance
    private static final Map<PositionCategory, FootballPosition[]> CACHE_BY_CATEGORY = 
        Arrays.stream(values())
            .collect(Collectors.groupingBy(
                FootballPosition::getCategory,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> list.toArray(new FootballPosition[0])
                )
            ));

    // ✅ Set pour isCentral()
    private static final Set<FootballPosition> CENTRAL_POSITIONS = EnumSet.of(
        GK, CB, CDM, CM, CAM, CF, ST, SW, SS, CWB
    );

    @JsonValue
    public String getName() {
        return this.name();
    }

    public String getI18nKey() {
        return "position." + this.name();
    }

    public boolean isDefender() { 
        return category == PositionCategory.DEFENDER; 
    }
    
    public boolean isMidfielder() { 
        return category == PositionCategory.MIDFIELDER; 
    }
    
    public boolean isForward() { 
        return category == PositionCategory.FORWARD; 
    }
    
    public boolean isGoalkeeper() { 
        return category == PositionCategory.GOALKEEPER; 
    }

    public boolean isRightSide() {
        return this.name().startsWith("R");
    }

    public boolean isLeftSide() {
        return this.name().startsWith("L");
    }

    // ✅ CORRIGÉ
    public boolean isCentral() {
        return CENTRAL_POSITIONS.contains(this);
    }

    // ✅ SIMPLIFIÉ
    public static Optional<FootballPosition> fromName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(valueOf(name.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    // ✅ AVEC CACHE
    public static FootballPosition[] getByCategory(PositionCategory category) {
        return CACHE_BY_CATEGORY.getOrDefault(category, new FootballPosition[0]);
    }

    @Getter
    public enum PositionCategory {
        GOALKEEPER,
        DEFENDER,
        MIDFIELDER,
        FORWARD;

        public String getI18nKey() {
            return "position.category." + this.name();
        }
    }
}