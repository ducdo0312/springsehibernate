package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.Lecturer;
import com.example.springsehibernate.Entity.Student;
import com.example.springsehibernate.Entity.StudentVersion;
import com.example.springsehibernate.Repository.StudentVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentVersionService {

    @Autowired
    private StudentVersionRepository studentVersionRepository;

//    public StudentVersion getStudentVersionByStudentAndVersionType(Student student, String versionType) {
//        return studentVersionRepository.findByStudentAndVersionType(student, versionType);
//    }

    public StudentVersion save(StudentVersion studentVersion) {
        return studentVersionRepository.save(studentVersion);
    }

//    public List<StudentVersion> getStudentVersionByType_Student_Lecturer_Department_DepartmentId(String versionType, Long departmentId) {
//        return studentVersionRepository.findByVersionTypeAndStudent_Lecturer_Department_DepartmentId(versionType, departmentId);
//    }



    public void updateVersionWithStudentData(StudentVersion version, Student student) {
        version.setLecturer(student.getLecturer());
        version.setStudentID(student.getStudentID());
        version.setName(student.getName());
        version.setAcademicYear(student.getAcademicYear());
        version.setDateOfBirth(student.getDateOfBirth());
        version.setDtbc(student.getDtbc());
        version.setNamelecturer(student.getNamelecturer());
        version.setUniversity(student.getUniversity());
        version.setThesistopics(student.getThesistopics());
        version.setNewTopics(student.getNewTopics());
        version.setLecturerReviewer(student.getLecturerReviewer());
        version.setLecturerReviewerWorkplace(student.getLecturerReviewerWorkplace());
        version.setSecondLecturerReviewer(student.getSecondLecturerReviewer());
        version.setSecondLecturerReviewerWorkplace(student.getSecondLecturerReviewerWorkplace());
        version.setSemester(student.getSemester());
        version.setStatus(student.getStatus());
        version.setNamesecondlecturer(student.getNamesecondlecturer());
        version.setFilePath(student.getFilePath());
    }

    public void updateVersionWithStudentDataDepartment(StudentVersion sv1, StudentVersion sv2) {
        sv1.setLecturer(sv2.getLecturer());
        sv1.setStudentID(sv2.getStudentID());
        sv1.setName(sv2.getName());
        sv1.setAcademicYear(sv2.getAcademicYear());
        sv1.setDateOfBirth(sv2.getDateOfBirth());
        sv1.setDtbc(sv2.getDtbc());
        sv1.setNamelecturer(sv2.getNamelecturer());
        sv1.setUniversity(sv2.getUniversity());
        sv1.setThesistopics(sv2.getThesistopics());
        sv1.setNewTopics(sv2.getNewTopics());
        sv1.setLecturerReviewer(sv2.getLecturerReviewer());
        sv1.setLecturerReviewerWorkplace(sv2.getLecturerReviewerWorkplace());
        sv1.setSecondLecturerReviewer(sv2.getSecondLecturerReviewer());
        sv1.setSecondLecturerReviewerWorkplace(sv2.getSecondLecturerReviewerWorkplace());
        sv1.setSemester(sv2.getSemester());
        sv1.setStatus(sv2.getStatus());
        sv1.setNamesecondlecturer(sv2.getNamesecondlecturer());
        sv1.setSecondLecturerWorkSpace(sv2.getSecondLecturerWorkSpace());
        sv1.setFilePath(sv2.getFilePath());
    }

    public List<StudentVersion> getStudentVersionByStudent_Lecturer_Department_DepartmentIdAndVersionType(Long departmentId, String versionType, String academicYear, int semester) {
        return studentVersionRepository.findByStudent_Lecturer_Department_DepartmentIdAndVersionTypeAndAcademicYearAndSemester(departmentId, versionType, academicYear, semester);
    }

    public List<StudentVersion> getStudentVersionByAcademicYearAndSemesterAndVersionTypeAndLecturer_Department_DepartmentId(String academicYear,
                                                                                                                  int semester,String versionType, Long departmentId) {
        return studentVersionRepository.findByAcademicYearAndSemesterAndVersionTypeAndLecturer_Department_DepartmentId(academicYear, semester,versionType, departmentId);
    }

//    public StudentVersion
//    getStudentVersionByVersionType_Student_Lecturer_Department_DepartmentIdAndStudent_Lecturer_Department_FacultyId
//            (String versionType,
//             Long departmentId,
//             Long facultyId){
//        return
//                studentVersionRepository.
//                        findByVersionTypeAndStudent_Lecturer_Department_DepartmentIdAndStudent_Lecturer_Department_FacultyId(versionType,
//                                departmentId, facultyId);
//    }

//    public List<StudentVersion> getStudentVersionByVersionTypeAndStudent_Lecturer_Department_FacultyId(String versionType, Long facultyId) {
//        return studentVersionRepository.findByVersionTypeAndStudent_Lecturer_Department_FacultyId(versionType, facultyId);
//    }
}
