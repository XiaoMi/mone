package run.mone.agentx.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import run.mone.agentx.agent.HiveManagerAgentService;
import run.mone.hive.schema.Message;

@RestController
@RequestMapping("/api/v1/hive-manager")
@RequiredArgsConstructor
public class HiveManagerController {

    private final HiveManagerAgentService hiveManagerAgentService;

    @PostMapping("/message")
    public Flux<String> receiveMessage(@RequestBody Message message) {
        return hiveManagerAgentService.receiveMsg(message);
    }
} 