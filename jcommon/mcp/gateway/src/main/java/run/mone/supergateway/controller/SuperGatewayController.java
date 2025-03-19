package run.mone.supergateway.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.supergateway.service.SseToStdioGateway;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class SuperGatewayController {

    @Autowired(required = false)
    private SseToStdioGateway sseToStdioGateway;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, SseEmitter> sessions = new ConcurrentHashMap<>();

    @GetMapping(value = "${supergateway.sse.path:/sse}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        String sessionId = UUID.randomUUID().toString();
        log.info("New SSE connection request, session ID: {}", sessionId);
        
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sessions.put(sessionId, emitter);
        
        // 发送初始消息，包含消息发送端点
        try {
            // 发送 endpoint 事件，包含 sessionId
            String endpoint = String.format("/message?sessionId=%s", sessionId);
            emitter.send(SseEmitter.event()
                .name("endpoint")
                .data(endpoint));
        } catch (IOException e) {
            log.error("Error sending initial message", e);
        }
        
        // 设置完成回调
        emitter.onCompletion(() -> {
            log.info("SSE connection completed for session: {}", sessionId);
            sessions.remove(sessionId);
        });
        
        // 设置超时回调
        emitter.onTimeout(() -> {
            log.info("SSE connection timeout for session: {}", sessionId);
            sessions.remove(sessionId);
        });
        
        // 设置错误回调
        emitter.onError(e -> {
            log.error("SSE connection error for session: {}", sessionId, e);
            sessions.remove(sessionId);
        });
        
        return emitter;
    }

    @PostMapping("${supergateway.message.path:/message}")
    public ResponseEntity<String> sendMessage(@RequestParam String sessionId, @RequestBody String message) {
        log.debug("Received message for session {}: {}", sessionId, message);
        
        // 检查会话是否存在
        if (!sessions.containsKey(sessionId)) {
            log.warn("Session not found: {}", sessionId);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Session not found");
        }
        
        try {
            // 验证JSON格式
            JsonNode jsonNode = objectMapper.readTree(message);
            
            if (sseToStdioGateway != null) {
                sseToStdioGateway.sendMessage(jsonNode);
                return ResponseEntity.ok("Message sent");
            } else {
                log.warn("SSE到Stdio网关未初始化，无法发送消息");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Gateway not initialized");
            }
        } catch (IOException e) {
            log.error("发送消息时出错", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format");
        }
    }

    @GetMapping("${supergateway.health.endpoint:/health}")
    public String health() {
        return "ok";
    }
} 