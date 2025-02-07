package run.mone.m78.service.asr.xiaoai.DTO;

import io.netty.channel.Channel;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

@Data
public class XiaoAiWebSocketDTO {

    private Channel channel;

    private String sessionId;

    private volatile boolean canSend;

//    private volatile boolean audioSendfinish;

    private volatile boolean handshakeComplate;

    private volatile boolean clientFinish;

    private volatile boolean xiaoaiFinish;


}
