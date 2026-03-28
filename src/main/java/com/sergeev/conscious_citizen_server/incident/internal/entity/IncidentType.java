package com.sergeev.conscious_citizen_server.incident.internal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "incident_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // PARKING, FOOD_EXPIRED
}
