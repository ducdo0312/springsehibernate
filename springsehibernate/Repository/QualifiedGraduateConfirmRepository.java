package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.QualifiedGraduate;
import com.example.springsehibernate.Entity.QualifiedGraduateConfirm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QualifiedGraduateConfirmRepository extends JpaRepository<QualifiedGraduateConfirm, Long> {

    // Phương thức kiểm tra sự tồn tại của bản ghi dựa trên năm học và kỳ học
    boolean existsByAcademicYearAndSemester(String academicYear, int semester);

    Page<QualifiedGraduateConfirm> findByAcademicYearAndSemester(String academicYear, int semester, Pageable pageable);

    Optional<QualifiedGraduateConfirm> findByStudentIdAndAcademicYearAndSemester(Long studentId, String academicYear,
                                                                                  int semester);
}
