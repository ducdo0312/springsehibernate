package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.Notification;
import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Service.NotificationService;
import com.example.springsehibernate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/notify/{lecturerId}")
    public String getNotifications(@PathVariable("lecturerId") long lecturerId, Model model) {
        List<Notification> notifications = notificationService.getNotificationsByLecturerId(lecturerId);
        model.addAttribute("notifications", notifications);
        model.addAttribute("userId", lecturerId);
        return "notify";
    }

}
