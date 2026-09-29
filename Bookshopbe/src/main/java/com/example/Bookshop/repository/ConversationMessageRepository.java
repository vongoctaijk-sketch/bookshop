package com.example.Bookshop.repository;

import com.example.Bookshop.entity.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationMessageRepository
        extends JpaRepository<ConversationMessage, Integer> {

    List<ConversationMessage> findByConversationIdOrderByCreatedAtAsc(
            Integer conversationId
    );
}