package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.MessageStatus;
import com.example.springsehibernate.Entity.Student;
import com.example.springsehibernate.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public List<Student> findByLecturerID(Long LecturerID) {
        return studentRepository.findByLecturerID(LecturerID);
    }

    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    public void getAllStudents() {
        studentRepository.findAll();
    }

    public Student getStudentById(Long ID) {
        return studentRepository.findStudentByID(ID);
    }

    public Student update(Student student) {
        // Kiểm tra xem sinh viên có tồn tại không trước khi cập nhật
        if (student != null && student.getID() != null && studentRepository.existsById(student.getID())) {
            return studentRepository.save(student);
        }
        throw new IllegalArgumentException("Student not found or ID is null");
    }


    public Page<Student> findByLecturerID(Long lecturerID, Pageable pageable) {
        return studentRepository.findByLecturerID(lecturerID, pageable);
    }

    public Page<Student> getStudentsByLecturerAndAcademicYear(Long lecturerId, String academicYear, PageRequest pageRequest) {
        return studentRepository.findByLecturerIDAndAcademicYear(lecturerId, academicYear, pageRequest);
    }

    public Page<Student> getStudentsByLecturerAndAcademicYearAndSemester(Long lecturerId, String academicYear, int semester, PageRequest pageRequest) {

        return studentRepository.findByLecturerIDAndAcademicYearAndSemester(lecturerId, academicYear, semester, pageRequest);
    }


    public List<Student> getStudentsByLecturerAndAcademicYearAndSemester(Long lecturerId, String academicYear, int semester) {
        return studentRepository.findByLecturerIDAndAcademicYearAndSemester(lecturerId, academicYear, semester);
    }



//    public List<Student> getConfirmedStudents() {
//        // Truy vấn cơ sở dữ liệu hoặc thực hiện các xử lý để lấy danh sách sinh viên đã xác nhận
//        return studentRepository.findByMessage_StatusEnum(MessageStatus.ACCEPTED);
//    }
//
//    public List<Student> findStudentsByLecturerAndMessageStatus(long lecturerId, MessageStatus status) {
//        return studentRepository.findByLecturerIDAndMessages_StatusEnum(lecturerId, status);
//    }
}
