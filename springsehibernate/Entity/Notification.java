package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime createdAt;

    // Thêm trường để lưu ID của giảng viên nhận thông báo
    @Column(name = "lecturer_id")
    private Long lecturerId;
}
