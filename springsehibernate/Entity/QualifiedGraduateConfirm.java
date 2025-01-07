package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "qualified_graduates_confirm")
@Data
public class QualifiedGraduateConfirm {
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

    protected QualifiedGraduateConfirm() {
        // Constructor không tham số được để trống hoặc chỉ khởi tạo mặc định các thuộc tính
    }
    public QualifiedGraduateConfirm(QualifiedGraduate graduate) {
        this.Name = graduate.getName();
        this.studentId = graduate.getStudentId();
        this.dateOfBirth = graduate.getDateOfBirth();
        this.nameClass = graduate.getNameClass();
        this.accumulatedCredits = graduate.getAccumulatedCredits();
        this.tbc = graduate.getTbc();
        this.academicYear = graduate.getAcademicYear();
        this.semester = graduate.getSemester();
    }

}
