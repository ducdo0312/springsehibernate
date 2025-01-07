package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Đặt các phương thức tìm kiếm hoặc xử lý dữ liệu thông báo ở đây
    List<Notification> findByLecturerId(Long lecturerId);

}

