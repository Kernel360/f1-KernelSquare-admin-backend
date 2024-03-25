package com.kernelsquare.domainmongodb.domain.alert.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Document(collection = "alert")
public class Alert {
    @Id
    private String id;

    @Indexed
    private String recipientId;

    private String recipient;

    private String senderId;

    private String sender;

    private AlertType alertType;

    private LocalDateTime sendTime;

    private Map<String, String> payload;

    @Getter
    @RequiredArgsConstructor
    public enum AlertType {
        QUESTION_REPLY,
        RANK_ANSWER,
        COFFEE_CHAT_REQUEST
    }
}
