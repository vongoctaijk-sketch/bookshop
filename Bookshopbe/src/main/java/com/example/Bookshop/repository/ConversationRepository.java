package com.example.Bookshop.repository;

import com.example.Bookshop.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository
        extends JpaRepository<Conversation, Integer> {
}