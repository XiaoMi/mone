
package run.mone.moner.server.controller;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import run.mone.moner.server.bo.SelectorConfig;
import run.mone.moner.server.service.ConfigService;
import run.mone.moner.server.websocket.WebSocketService;

@Controller
public class ChatController {

    @Resource
    private WebSocketService service;

    @GetMapping("/chat")
    public String chatPage() {
        return "chat.html";
    }

    @PostMapping("/send")
    @ResponseBody
    public void handleMessage(@RequestBody String message) {
        service.sendMessageToAllClients(message);
    }
    
}
