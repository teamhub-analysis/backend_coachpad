package com.coachpad.model;

import com.coachpad.persistence.Enum.ShapeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldShape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ShapeType type;

    // 📏 Coordonnées des lignes
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;

    // ⚪ Coordonnées du cercle
    private Double centerX;
    private Double centerY;
    private Double radius;

    // ⬛ Coordonnées du rectangle
    private Double rectX;
    private Double rectY;
    private Double rectWidth;
    private Double rectHeight;

    // 🔗 Lien vers le terrain
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private com.coachpad.model.Field field; // ✅ référence à ton entité Field
}
