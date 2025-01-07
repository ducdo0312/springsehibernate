package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "departments")
public class Department {
    @Id
    @Column(name = "ID")
    private Long departmentId;

    @Column(name = "Name")
    private String name;

    @Column(name = "FacultyID")
    private Long facultyId;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lecturer> lecturers;
}
