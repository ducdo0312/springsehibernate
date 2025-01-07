package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "student_version")
public class StudentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "ID")
    private Student student;

    @Column(name = "version_type") // giảng viên, Bộ môn, Khoa
    private String versionType;

    @Column(name = "version_date")
    private Date versionDate;

    @Column(name = "StudentID")
    private Long StudentID;

    @Column(name = "Name")
    private String Name;

    @Column(name = "DateOfBirth")
    private LocalDate DateOfBirth;  // Lưu ý: Tôi đã thay LocalDate bằng Date ở đây

    @Column(name = "Thesistopics")
    private String Thesistopics;

    private String NewTopics;

    @Column(name = "dtbc")
    private Float dtbc;

    @Column(name = "university")
    private String university;

    @Column(name = "namelecturer")
    private String namelecturer;

    @Column(name = "LecturerReviewer")
    private String LecturerReviewer;

    @Column(name = "SecondLecturerReviewer")
    private String SecondLecturerReviewer;

    @Column(name = "LecturerReviewerWorkplace")
    private String LecturerReviewerWorkplace;

    @Column(name = "SecondLecturerReviewerWorkplace")
    private String SecondLecturerReviewerWorkplace;

    @Column(name = "namesecondlecturer")
    private String namesecondlecturer;

    private String secondLecturerWorkSpace;

    private String academicYear;

    @Column(name = "Semester")
    private int Semester;

    @Column(name = "status")
    private String status;

    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturerID")
    private Lecturer lecturer;  // Mối quan hệ với bảng Lecturer

    @Override
    public String toString() {
        return "StudentVersion{" +
                "id=" + id +
                ", student=" + (student != null ? student.getID() : null) +
                ", versionType='" + versionType + '\'' +
                ", versionDate=" + versionDate +
                ", StudentID=" + StudentID +
                ", Name='" + Name + '\'' +
                ", DateOfBirth=" + DateOfBirth +
                ", Thesistopics='" + Thesistopics + '\'' +
                ", NewTopics='" + NewTopics + '\'' +
                ", dtbc=" + dtbc +
                ", university='" + university + '\'' +
                ", namelecturer='" + namelecturer + '\'' +
                ", LecturerReviewer='" + LecturerReviewer + '\'' +
                ", SecondLecturerReviewer='" + SecondLecturerReviewer + '\'' +
                ", LecturerReviewerWorkplace='" + LecturerReviewerWorkplace + '\'' +
                ", SecondLecturerReviewerWorkplace='" + SecondLecturerReviewerWorkplace + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", Semester=" + Semester +
                ", status='" + status + '\'' +
                ", lecturer=" + (lecturer != null ? lecturer.getId() : null) +
                '}';
    }

    public boolean equals(Student student) {
        if (student == null) return false;

        return Objects.equals(this.StudentID, student.getStudentID())
                && Objects.equals(this.Name, student.getName())
                && Objects.equals(this.DateOfBirth, student.getDateOfBirth())
                && Objects.equals(this.Thesistopics, student.getThesistopics())
                && Objects.equals(this.NewTopics, student.getNewTopics())
                && Objects.equals(this.dtbc, student.getDtbc())
                && Objects.equals(this.university, student.getUniversity())
                && Objects.equals(this.namelecturer, student.getNamelecturer())
                && Objects.equals(this.LecturerReviewer, student.getLecturerReviewer())
                && Objects.equals(this.SecondLecturerReviewer, student.getSecondLecturerReviewer())
                && Objects.equals(this.LecturerReviewerWorkplace, student.getLecturerReviewerWorkplace())
                && Objects.equals(this.SecondLecturerReviewerWorkplace, student.getSecondLecturerReviewerWorkplace())
                && Objects.equals(this.academicYear, student.getAcademicYear())
                && Objects.equals(this.Semester, student.getSemester())
                && Objects.equals(this.status, student.getStatus());
    }

    public boolean StudentVersionEqual(StudentVersion sv) {
        if (sv == null) return false;
        return Objects.equals(this.StudentID, sv.getStudentID())
                && Objects.equals(this.Name, sv.getName())
        && Objects.equals(this.DateOfBirth, sv.getDateOfBirth())
                && Objects.equals(this.Thesistopics, sv.getThesistopics())
                && Objects.equals(this.NewTopics, sv.getNewTopics())
                && Objects.equals(this.dtbc, sv.getDtbc())
                && Objects.equals(this.university, sv.getUniversity())
                && Objects.equals(this.namelecturer, sv.getNamelecturer())
                && Objects.equals(this.SecondLecturerReviewer, sv.getSecondLecturerReviewer())
                && Objects.equals(this.LecturerReviewerWorkplace, sv.getLecturerReviewerWorkplace())
                && Objects.equals(this.SecondLecturerReviewerWorkplace, sv.getSecondLecturerReviewerWorkplace())
                && Objects.equals(this.academicYear, sv.getAcademicYear())
                && Objects.equals(this.Semester, sv.getSemester())
                && Objects.equals(this.status, sv.getStatus());
    }


}
