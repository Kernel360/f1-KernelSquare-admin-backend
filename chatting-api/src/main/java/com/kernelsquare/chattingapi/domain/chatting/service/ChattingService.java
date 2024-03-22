package com.kernelsquare.chattingapi.domain.chatting.service;

import com.kernelsquare.chattingapi.domain.chatting.dto.ChatMessageRequest;
import com.kernelsquare.chattingapi.domain.chatting.dto.ChatMessageResponse;
import com.kernelsquare.domainmongodb.domain.coffeechat.entity.MongoChatMessage;
import com.kernelsquare.domainmongodb.domain.coffeechat.repository.MongoChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final SimpMessageSendingOperations sendingOperations;
    private final MongoChatMessageRepository mongoChatMessageRepository;

    @KafkaListener(topics = "chatting", groupId = "chatting", containerFactory = "chattingKafkaListenerContainerFactory")
    public void sendMessage(ChatMessageRequest message) {
        ChatMessageResponse responseMessage = ChatMessageResponse.convertResponse(message);
        sendingOperations.convertAndSend("/topic/chat/room/" + responseMessage.getRoomKey(), responseMessage);
    }

    @KafkaListener(topics = "chatting", groupId = "mongo", containerFactory = "mongoKafkaListenerContainerFactory")
    public void storeMessage(ChatMessageRequest message) {
        MongoChatMessage mongoChatMessage = ChatMessageRequest.toMongoChatMessage(message);
        mongoChatMessageRepository.save(mongoChatMessage);
    }
}
