package com.kernelsquare.chattingapi.domain.chatting.dto;

import com.kernelsquare.core.type.MessageType;
import com.kernelsquare.domainmongodb.domain.coffeechat.entity.MongoChatMessage;
import com.kernelsquare.domainmongodb.domain.coffeechat.entity.MongoMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    private MessageType type;

    private String roomKey;

    private Long senderId;

    private String sender;

    private String senderImageUrl;

    private String message;

    private LocalDateTime sendTime;

    private List<ChatRoomMember> memberList;

    public void setMessage(String message) {
        this.message = message;
    }

    public static MongoChatMessage toMongoChatMessage(ChatMessageRequest message) {
        return MongoChatMessage.builder()
            .roomKey(message.getRoomKey())
            .type(MongoMessageType.valueOf(String.valueOf(message.getType())))
            .senderId(message.getSenderId())
            .sender(message.getSender())
            .senderImageUrl(message.getSenderImageUrl())
            .message(message.getMessage())
            .sendTime(message.getSendTime())
            .build();
    }
}
