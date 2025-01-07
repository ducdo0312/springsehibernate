package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.Message;
import com.example.springsehibernate.Entity.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdAndStatusEnum(Long receiverId, MessageStatus status);

    List<Message> findAllBySenderId(Long senderId);

    List<Message> findAllByReceiverId(Long senderId);

    Optional<Message> findTopBySenderIdOrderBySentAtDesc(Long senderId);
}
