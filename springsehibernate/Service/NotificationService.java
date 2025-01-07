package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.Notification;
import com.example.springsehibernate.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotificationToLecturer(String content, Long senderId) {
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setLecturerId(senderId);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByLecturerId(long lecturerId) {
        // Thực hiện truy vấn để lấy danh sách thông báo theo lecturerId
        return notificationRepository.findByLecturerId(lecturerId);
    }

}

