package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.TimePhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TimePhaseRepository extends JpaRepository<TimePhase, Long> {
    @Query("SELECT tp FROM TimePhase tp WHERE :currentDate BETWEEN tp.phase1Start AND tp.phase3End")
    Optional<TimePhase> findCurrentTimePhase(@Param("currentDate") LocalDate currentDate);

    // Kiểm tra xem bảng có chứa bất kỳ bản ghi nào không
    boolean existsByIdIsNotNull();
}
