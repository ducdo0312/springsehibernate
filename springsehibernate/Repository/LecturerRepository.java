package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    Optional<Lecturer> findById(Long id);

    Lecturer findByName(String name);

    List<Lecturer> findByNameContaining(String name);

    List<Lecturer> findByDepartment_DepartmentId(Long departmentId);

}

