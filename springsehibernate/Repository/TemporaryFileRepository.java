package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.TemporaryFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporaryFileRepository extends JpaRepository<TemporaryFile, Long> {
}
