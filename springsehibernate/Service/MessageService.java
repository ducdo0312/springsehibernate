package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.Lecturer;
import com.example.springsehibernate.Entity.Message;
import com.example.springsehibernate.Entity.MessageStatus;
import com.example.springsehibernate.Repository.LecturerRepository;
import com.example.springsehibernate.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public List<Message> getMessagesForUser(Long userId) {
        return messageRepository.findByReceiverIdAndStatusEnum(userId, MessageStatus.UNSEEN);
    }
}

