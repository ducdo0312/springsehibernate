package com.example.springsehibernate.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "students")
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "StudentID")
    private Long StudentID;

    @Column(name = "Name")
    private String Name;

    @Column(name = "DateOfBirth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate DateOfBirth;

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
    private String LecturerReviewer; // Giảng viên phản biện thứ nhất

    @Column(name = "SecondLecturerReviewer")
    private String SecondLecturerReviewer; // Giảng viên phản biện thứ hai

    @Column(name = "LecturerReviewerWorkplace")
    private String LecturerReviewerWorkplace; // Nơi công tác của giảng viên phản biện

    @Column(name = "SecondLecturerReviewerWorkplace")
    private String SecondLecturerReviewerWorkplace; // Nơi công tác của giảng viên phản biện

    private String filePath;

    private String academicYear;

    private int Semester;

    @Column(name = "status")
    private String status;


   @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LecturerID")
    private Lecturer lecturer;

    @Column(name = "SecondLecturerID")
    private Long secondLecturerId;

    @Column(name = "namesecondlecturer")
    private String namesecondlecturer;

    private String secondLecturerWorkSpace;

    @JsonIgnore
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ConfirmTable confirmTable;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @Override
    public String toString() {
        return "Student{" +
                "id=" + ID +
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
                '}';
    }
}
