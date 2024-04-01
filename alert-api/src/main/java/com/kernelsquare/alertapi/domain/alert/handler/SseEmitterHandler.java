package com.kernelsquare.alertapi.domain.alert.handler;

import com.kernelsquare.alertapi.domain.alert.dto.AlertDto;
import com.kernelsquare.domainmongodb.domain.alert.entity.Alert;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterHandler {
    private static final Long DEFAULT_TIMEOUT = 60 * 60 * 1000L;
    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void addEmitter(Long memberId, SseEmitter emitter) {
        emitters.put(memberId, emitter);
    }

    public SseEmitter getEmitter(Long memberId) {
        return emitters.get(memberId);
    }

    public void deleteEmitter(Long memberId) {
        emitters.remove(memberId);
    }

    public SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        addEmitter(memberId, emitter);

        emitter.onCompletion(() -> deleteEmitter(memberId));
        emitter.onTimeout(() -> deleteEmitter(memberId));
        emitter.onError(e -> deleteEmitter(memberId));

        if (Objects.nonNull(emitter)) {
            try {
                emitter.send(SseEmitter.event()
                    .name("연결 확인")
                    .data("연결 확인", MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                deleteEmitter(memberId);
                emitter.completeWithError(e);
            }
        }

        return emitter;
    }

    public void sendEmitter(Alert alert) {
        SseEmitter emitter = getEmitter(Long.valueOf(alert.getRecipientId()));

        AlertDto.MessageResponse data = AlertDto.MessageResponse.from(alert);

        if (Objects.nonNull(emitter)) {
            try {
                emitter.send(SseEmitter.event()
                    .id(data.id())
                    .name(data.alertType().name())
                    .data(data, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                deleteEmitter(Long.valueOf(alert.getRecipientId()));
                emitter.completeWithError(e);
            }
        }
    }

    public ConcurrentHashMap<Long, SseEmitter> getEmitters() {
        return emitters;
    }
}
