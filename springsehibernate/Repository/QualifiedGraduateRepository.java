package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.QualifiedGraduate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualifiedGraduateRepository extends JpaRepository<QualifiedGraduate, Long> {

    Page<QualifiedGraduate> findByAcademicYearAndSemester(String academicYear, int semester, Pageable pageable);
    
    List<QualifiedGraduate> findByAcademicYearAndSemester(String academicYear, int semester);
    boolean existsByAcademicYearAndSemester(String academicYear, Integer semester);

    // Kiểm tra xem bảng có chứa bất kỳ bản ghi nào không
    boolean existsByIdIsNotNull();
}
