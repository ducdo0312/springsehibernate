package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "temporary_file")
public class TemporaryFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String contentType;

    @Lob
    private byte[] data;

    // Standard getters and setters
}
