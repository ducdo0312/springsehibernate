package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.Department;
import com.example.springsehibernate.Entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByFacultyId(Long facultyId);
}
