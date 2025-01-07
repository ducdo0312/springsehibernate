package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.Lecturer;
import com.example.springsehibernate.Entity.Student;
import com.example.springsehibernate.Repository.LecturerRepository;
import com.example.springsehibernate.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LecturerService {

    @Autowired
    private LecturerRepository lecturerRepository;

    public List<Lecturer> findAll() {
        return lecturerRepository.findAll();
    }

    public List<Lecturer> findByNameContaining(String name) {
        return lecturerRepository.findByNameContaining(name);
    }

}
