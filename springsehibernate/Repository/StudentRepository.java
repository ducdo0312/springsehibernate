package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.MessageStatus;
import com.example.springsehibernate.Entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s FROM Student s WHERE s.lecturer.id = :LecturerID")
    List<Student> findByLecturerID(@Param("LecturerID") Long LecturerID);

    Student findStudentByID(Long ID);

    @Query("SELECT s FROM Student s WHERE s.lecturer.id = :lecturerID")
    Page<Student> findByLecturerID(@Param("lecturerID") Long lecturerID, Pageable pageable);

    Optional<Student> findByID(Long ID);

    @Query("SELECT s FROM Student s WHERE s.StudentID = :StudentID")
    Optional<Student> findByStudentID(Long StudentID);

    @Query("SELECT s FROM Student s WHERE s.lecturer.id = :lecturerId AND s.academicYear = :academicYear")
    Page<Student> findByLecturerIDAndAcademicYear(Long lecturerId, String academicYear, Pageable pageable);
    @Query("SELECT s FROM Student s WHERE (s.lecturer.id = :lecturerId OR s.secondLecturerId = :lecturerId) AND s.academicYear = :academicYear AND s.Semester = :semester")
    Page<Student> findByLecturerIDAndAcademicYearAndSemester(
            @Param("lecturerId") Long lecturerId,
            @Param("academicYear") String academicYear,
            @Param("semester") int semester,
            Pageable pageable);


    @Query("SELECT s FROM Student s WHERE s.lecturer.id = :lecturerId AND s.academicYear = :academicYear AND s.Semester = :semester")
    List<Student> findByLecturerIDAndAcademicYearAndSemester(Long lecturerId, String academicYear, int semester);

    @Query("SELECT s FROM Student s WHERE s.StudentID = :studentID AND s.status = :status")
    Optional<Student> findByStudentIDAndStatus(Long studentID, String status);

    @Query("SELECT s FROM Student s WHERE s.StudentID = :studentID AND s.academicYear = :academicYear AND s.Semester = :semester")
    Optional<Student> findByStudentIDAndAcademicYearAndSemester(Long studentID, String academicYear, int semester);

//    List<Student> findByMessage_StatusEnum(MessageStatus status);
//
//    List<Student> findByLecturerIDAndMessages_StatusEnum(long lecturerId, MessageStatus status);
}
