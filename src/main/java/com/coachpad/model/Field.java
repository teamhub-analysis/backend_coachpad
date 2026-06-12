package com.coachpad.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

import com.coachpad.model.enums.FieldType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;              // Nom du terrain (ex: "Terrain principal")
    private double widthM;            // Largeur en mètres
    private double heightM;           // Hauteur en mètres
    private double orientationDeg;    // Orientation en degrés (rotation)

    @Enumerated(EnumType.STRING)
    private FieldType type;           // Type du terrain

    // 🔗 Relation avec les formes du terrain
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FieldShape> shapes = new ArrayList<>();
}
