package com.example.springsehibernate.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "lecturers")
@Data
public class Lecturer {
    @Id
    private Long id;

    @Column(name = "Name")
    private String name;

    private String gmail;

    @JsonIgnore
    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> students;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DepartmentID")
    private Department department;

}
