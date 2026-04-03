package com.sergeev.conscious_citizen_server.document.internal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long userId;

    private Long incidentId;

    @Lob
    private byte[] content; // PDF

    private String contentType; // application/pdf

    private LocalDateTime createdAt;
}
