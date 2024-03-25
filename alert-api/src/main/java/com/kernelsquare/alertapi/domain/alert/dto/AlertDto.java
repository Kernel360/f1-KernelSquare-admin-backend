package com.kernelsquare.alertapi.domain.alert.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kernelsquare.core.constants.TimeResponseFormat;
import com.kernelsquare.domainmongodb.domain.alert.entity.Alert;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AlertDto {
    @Builder
    public record FindAllResponse(
        String recipientId,
        String recipient,
        String senderId,
        String sender,
        Alert.AlertType alertType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TimeResponseFormat.PATTERN)
        LocalDateTime sendTime
    ) {}

    @Builder
    public record PersonalFindAllResponse(
        List<AlertDto.MessageResponse> personalAlertList
    ) {
        public static PersonalFindAllResponse from(List<AlertDto.MessageResponse> personalAlert) {
            return new PersonalFindAllResponse(
                personalAlert
            );
        }
    }

    @Builder
    public record MessageResponse(
        String id,
        Alert.AlertType alertType,
        String recipient,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TimeResponseFormat.PATTERN)
        LocalDateTime sendTime,
        Map<String, String> payload
    ) {
        public static MessageResponse from(Alert alert) {
            return MessageResponse.builder()
                .id(alert.getId())
                .alertType(alert.getAlertType())
                .recipient(alert.getRecipient())
                .sendTime(alert.getSendTime())
                .payload(alert.getPayload())
                .build();
        }
    }

}
