package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.Student;
import com.example.springsehibernate.Entity.StudentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentVersionRepository extends JpaRepository<StudentVersion, Long> {
    StudentVersion findByStudentAndVersionType(Student student, String versionType);

    @Query("SELECT sv FROM StudentVersion sv WHERE sv.StudentID = :studentId AND sv.academicYear = :academicYear AND sv.Semester = :semester AND sv.versionType = :versionType")
    StudentVersion findByVersionTypeAndAcademicYearAndSemesterAndStudentID(String versionType, String academicYear, int semester, Long studentId);

    @Query("SELECT sv FROM StudentVersion sv WHERE sv.lecturer.department.departmentId = :departmentID AND sv.academicYear = :academicYear AND sv.Semester = :semester AND sv.versionType = :versionType")
    List<StudentVersion> findByVersionTypeAndLecturer_Department_DepartmentIdAndAcademicYearAndSemester(String versionType,
                                                                                                         Long departmentID,
                                                                                                         String academicYear,
                                                                                                        int semester);

    List<StudentVersion> findByVersionTypeAndStudent_Lecturer_Department_DepartmentId(String versionType, Long departmentId);


    @Query("SELECT sv FROM StudentVersion sv WHERE sv.student.lecturer.department.departmentId = :departmentID AND sv.versionType = :versionType AND sv.academicYear = :academicYear AND sv.Semester = :semester")
    List<StudentVersion> findByStudent_Lecturer_Department_DepartmentIdAndVersionTypeAndAcademicYearAndSemester(Long departmentID, String versionType, String academicYear, int semester);

    @Query("SELECT sv FROM StudentVersion sv WHERE sv.academicYear = :academicYear AND sv.Semester = :semester AND sv.versionType = :versionType AND sv.lecturer.department.departmentId = :departmentId")
    List<StudentVersion> findByAcademicYearAndSemesterAndVersionTypeAndLecturer_Department_DepartmentId(String academicYear,
                                                                                                  int semester,String versionType, Long departmentId);

    @Query("SELECT sv FROM StudentVersion sv WHERE sv.versionType = :versionType AND sv.student.lecturer.department.departmentId = :departmentId AND sv.student.lecturer.department.facultyId = :facultyId")
    StudentVersion findByVersionTypeAndStudent_Lecturer_Department_DepartmentIdAndStudent_Lecturer_Department_FacultyId(String versionType,
                                                                                                                                 Long departmentId,Long facultyId);

    List<StudentVersion> findByVersionTypeAndStudent_Lecturer_Department_FacultyId(String VersionType, Long facultyId);


    @Query("SELECT sv FROM StudentVersion sv WHERE sv.versionType = :versionType AND sv.academicYear = :academicYear AND sv.Semester = :semester")
    List<StudentVersion> findByVersionTypeAndAcademicYearAndSemester(String versionType, String academicYear, int semester);


    @Query("SELECT sv FROM StudentVersion sv WHERE sv.lecturer.id = :lecturerId AND sv.academicYear = :academicYear AND sv.Semester = :semester AND sv.versionType = :versionType")
    List<StudentVersion> findByLecturer_IdAndAcademicYearAndSemesterAndVersionType(Long lecturerId,
                                                                                    String academicYear,
                                                                                                   int semester,
                                                                                                   String versionType);
}
