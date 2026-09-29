package com.example.Bookshop.service;

import com.example.Bookshop.entity.Conversation;
import com.example.Bookshop.entity.ConversationMessage;
import com.example.Bookshop.entity.MessageRole;
import com.example.Bookshop.repository.ConversationMessageRepository;
import com.example.Bookshop.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;

    public Conversation createConversation() {

        Conversation conversation = new Conversation();

        return conversationRepository.save(conversation);
    }

    public Conversation getConversation(Integer conversationId) {

        return conversationRepository.findById(conversationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Không tìm thấy conversation: " + conversationId
                        )
                );
    }

    public void saveMessage(
            Integer conversationId,
            MessageRole role,
            String content
    ) {

        Conversation conversation = getConversation(conversationId);

        ConversationMessage message = new ConversationMessage();

        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);

        messageRepository.save(message);
    }

    public List<ConversationMessage> getMessages(
            Integer conversationId
    ) {

        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);
    }
}