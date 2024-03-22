package com.kernelsquare.chattingapi.domain.chatting.controller;

import com.kernelsquare.chattingapi.domain.chatting.dto.ChatMessageRequest;
import com.kernelsquare.core.common_response.error.code.CoffeeChatErrorCode;
import com.kernelsquare.core.common_response.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChattingController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @MessageMapping("/chat/message")
    public void messageHandler(ChatMessageRequest message) {
        switch (message.getType()) {
            case ENTER -> message.setMessage(message.getSender() + "님이 입장하였습니다.");
            case TALK -> {}
            case CODE -> {}
            case LEAVE -> message.setMessage(message.getSender() + "님이 퇴장하였습니다.");
            case EXPIRE -> {}
            default -> throw new BusinessException(CoffeeChatErrorCode.MESSAGE_TYPE_NOT_VALID);
        }
        kafkaTemplate.send("chatting", message);
    }

    @GetMapping("/environment")
    public String getEnv() {
        return "chatting";
    }
}
