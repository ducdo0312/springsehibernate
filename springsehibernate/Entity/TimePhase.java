package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "time_phases")
@Data
public class TimePhase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phase1_start")
    private LocalDate phase1Start;

    @Column(name = "phase1_end")
    private LocalDate phase1End;

    @Column(name = "phase2_start")
    private LocalDate phase2Start;

    @Column(name = "phase2_end")
    private LocalDate phase2End;

    @Column(name = "phase3_start")
    private LocalDate phase3Start;

    @Column(name = "phase3_end")
    private LocalDate phase3End;

    public String getFormattedPhase1Start() {
        return phase1Start.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getFormattedPhase1End() {
        return phase1End.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getFormattedPhase2Start() {
        return phase2Start.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getFormattedPhase2SEnd() {
        return phase2End.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getFormattedPhase3Start() {
        return phase3Start.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getFormattedPhase3End() {
        return phase3End.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
