package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "qualified_graduates")
@Data
public class QualifiedGraduate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    private String Name;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE) // Sử dụng TemporalType.DATE để chỉ lưu ngày
    private Date dateOfBirth;

    private String nameClass;

    @Column(name = "accumulated_credits")
    private int accumulatedCredits;

    private Float tbc;

    private String academicYear;

    private int semester;
}
