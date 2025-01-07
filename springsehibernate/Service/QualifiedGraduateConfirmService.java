package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.AcademicYearUtil;
import com.example.springsehibernate.Entity.QualifiedGraduateConfirm;
import com.example.springsehibernate.Repository.QualifiedGraduateConfirmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QualifiedGraduateConfirmService {

    @Autowired
    private QualifiedGraduateConfirmRepository qualifiedGraduateConfirmRepository;

    public List<QualifiedGraduateConfirm> getAll() {
        return qualifiedGraduateConfirmRepository.findAll();
    }

    // Phương thức kiểm tra trùng lặp
    public boolean checkDuplicate(String year, int semester) {
        return qualifiedGraduateConfirmRepository.existsByAcademicYearAndSemester(year, semester);
    }

    public Page<QualifiedGraduateConfirm> getQualifiedGraduateConfirmPage(int pageNumber,
                                                                          int pageSize,
                                                                          String academicYear,
                                                                          Integer semester) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
        int currentSemester = AcademicYearUtil.getCurrentSemester();
        if (academicYear != null && semester != null) {
            return qualifiedGraduateConfirmRepository.findByAcademicYearAndSemester(academicYear, semester, pageable);
        } else {
            // Nếu không có thông tin về năm học và kỳ học, trả về danh sách theo năm học hiện tại
            return qualifiedGraduateConfirmRepository.findByAcademicYearAndSemester(currentAcademicYear, currentSemester, pageable);
        }

    }
}
