package com.kernelsquare.alertapi.domain.alert.manager;

import com.kernelsquare.alertapi.domain.alert.handler.SseEmitterHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AlertSchedulerManagerImpl implements AlertSchedulerManager {
    private final SseEmitterHandler sseEmitterHandler;

    @Override
    @Scheduled(fixedDelay = 40000)
    public void sendHeartbeat() {
        ConcurrentHashMap<Long, SseEmitter> emitters = sseEmitterHandler.getEmitters();
        // 모든 연결된 SseEmitter에게 빈 메시지를 보냄
        for (Map.Entry<Long, SseEmitter> entry : emitters.entrySet()) {
            SseEmitter emitter = entry.getValue();
            try {
                emitter.send(SseEmitter.event()
                    .name("연결 확인")
                    .data("연결 확인", MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                // 에러가 발생하면 해당 emitter를 제거
                sseEmitterHandler.deleteEmitter(entry.getKey());
            }
        }
    }
}
