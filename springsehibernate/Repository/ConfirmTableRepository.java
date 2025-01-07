package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.ConfirmTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmTableRepository extends JpaRepository<ConfirmTable, Long> {
    public List<ConfirmTable> findByDepartmentId(Long DepartmentId);

    List<ConfirmTable> findByFacultyId(Long facultyId);

    ConfirmTable findByStudentIDAndLecturerId(Long StudentID, Long LecturerID);
}
