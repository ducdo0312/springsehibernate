package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "receiver_id")
    private long receiverId;

    @Column(columnDefinition = "TEXT", name = "message_content")
    private String messageContent;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "status_enum")
    @Enumerated(EnumType.STRING)
    private MessageStatus statusEnum = MessageStatus.UNSEEN;

    @Column(name = "sent_at")
    private LocalDateTime sentAt; // Thêm trường này

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<Student> students = new ArrayList<>(); // Quan hệ một-nhiều với sinh viên

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<ConfirmTable> confirmTables = new ArrayList<>(); // Quan hệ một-nhiều với bảng xác nhận

}
