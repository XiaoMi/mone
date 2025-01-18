package run.mone.m78.service.asr.xiaoai.client;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.WebSocketSession;
import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedRes;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.ws.WsSessionHolder;
import run.mone.m78.service.asr.xiaoai.DTO.AudioMessageRespDTO;
import run.mone.m78.service.asr.xiaoai.DTO.XiaoAiFrameDTO;
import run.mone.m78.service.asr.xiaoai.DTO.XiaoAiWebSocketDTO;
import run.mone.m78.service.asr.xiaoai.XiaoAiService;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import static io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE;

@Slf4j
public class WebScoketClientFrameHandler extends SimpleChannelInboundHandler<Object> {

    public static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1,new DefaultThreadFactory("clean-invalidChannel-executor"));

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object frame)  {
        String sessionId = ctx.channel().attr(XiaoAiService.XIAOAI_SERVER_CHANNEL_DTO).get();
        // 处理 WebSocket 服务器返回的消息
        if (frame instanceof TextWebSocketFrame) {
            // 处理文本消息
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            String text = textFrame.text();
            log.warn("sessionId={},接收小爱返回数据，text={}",sessionId, text);
            XiaoAiFrameDTO xiaoAiFrameDTO = JSON.parseObject(text, XiaoAiFrameDTO.class);

            XiaoAiWebSocketDTO xiaoAiWebSocketDTO = XiaoAiService.XIAOAI_WEB_SOCKET_CLIENT_MAP.get(sessionId);
            if(xiaoAiWebSocketDTO != null){
                if(xiaoAiFrameDTO.getHeader().getName().equals("SpeechRecognizeResult")){
                    if(StringUtils.isNotBlank(xiaoAiFrameDTO.getPayload().getQuery()) ){
                        AsrRecognizedRes.AsrRecognizedData data = AsrRecognizedRes.AsrRecognizedData.builder().text(xiaoAiFrameDTO.getPayload().getQuery()).isFinal(xiaoAiFrameDTO.getPayload().getIs_final()).build();
//                        AsrRecognizedRes res = AsrRecognizedRes.builder().code(0).message("success").data(data).build();
//                        AudioMessageRespDTO audioMessageRespDTO = AudioMessageRespDTO.builder().data(xiaoAiFrameDTO.getPayload().getQuery()).isFinal(xiaoAiFrameDTO.getPayload().getIs_final()).build();
                   //     WsSessionHolder.INSTANCE.sendMsgBySessionId(xiaoAiWebSocketDTO.getSessionId(),JSON.toJSONString(res) , WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_RESULT, "");
                        sendRecognizedDataBySessionId(xiaoAiWebSocketDTO.getSessionId(),data);
                    }
                }else if(xiaoAiFrameDTO.getHeader().getName().equals("SpeechRecognizeFinish")){
                    log.warn("sessionId={},小爱推送断开消息",sessionId);
                    xiaoAiWebSocketDTO.setXiaoaiFinish(true);
                    sendMessageBySessionId(sessionId, 0, "end", WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_END);
                }else if(xiaoAiFrameDTO.getHeader().getName().equals("SpeechRecognizeError")){
                    log.error("sessionId={},小爱推送异常数据，text={}",sessionId,text);
                }
            }
        } else if (frame instanceof BinaryWebSocketFrame) {
            // 处理二进制消息
            BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
            ByteBuf byteBuf = binaryFrame.content();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            log.info("Received binary frame: " + new String(bytes));
        } else if (frame instanceof PongWebSocketFrame) {
            log.info("Received pong frame");
        } else if (frame instanceof CloseWebSocketFrame) {
            log.info("Received close frame");
            ctx.close();
        } else {
            log.error("sessionId={},Unsupported frame type{} " ,sessionId, frame.getClass().getName());
        }
    }
    public static void sendMessageBySessionId(String sessionId, Integer code, String message, String messageType) {
        String result = AsrRecognizedRes.generateCodeMsg(code, message);
        WsSessionHolder.INSTANCE.sendMsgBySessionId(sessionId, result, messageType);
    }

    public static void sendRecognizedDataBySessionId(String sessionId, AsrRecognizedRes.AsrRecognizedData data) {
        String result = AsrRecognizedRes.generateRecognizedData(data);
        WsSessionHolder.INSTANCE.sendMsgBySessionId(sessionId, result, WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_RESULT);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String sessionId = ctx.channel().attr(XiaoAiService.XIAOAI_SERVER_CHANNEL_DTO).get();
        log.error("sessionId={},与小爱链接exceptionCaught",sessionId, cause);
        super.exceptionCaught(ctx,cause);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        String sessionId = ctx.channel().attr(XiaoAiService.XIAOAI_SERVER_CHANNEL_DTO).get();
        XiaoAiWebSocketDTO xiaoAiWebSocketDTO = XiaoAiService.XIAOAI_WEB_SOCKET_CLIENT_MAP.get(sessionId);
        xiaoAiWebSocketDTO.setXiaoaiFinish(true);
        log.warn("sessionId={},与小爱链接channelUnregistered",sessionId);
        if(StringUtils.isNotBlank(sessionId)){
            WebSocketSession webSocketSession = XiaoAiService.M78_WEB_SOCKET_SESSION_MAP.get(sessionId);
            if(webSocketSession != null){
                webSocketSession.close();
            }
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String sessionId = ctx.channel().attr(XiaoAiService.XIAOAI_SERVER_CHANNEL_DTO).get();
        log.info("sessionId={},与小爱链接channelActive",sessionId);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String sessionId = ctx.channel().attr(XiaoAiService.XIAOAI_SERVER_CHANNEL_DTO).get();
        log.info("sessionId={},与小爱链接channelInactive",sessionId);
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        String sessionId = ctx.channel().attr(XiaoAiService.XIAOAI_SERVER_CHANNEL_DTO).get();
        log.info("sessionId={},与小爱链接channelReadComplete",sessionId);
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String sessionId = ctx.channel().attr(XiaoAiService.XIAOAI_SERVER_CHANNEL_DTO).get();
        log.warn("sessionId={},userEventTriggered evt={}",sessionId,JSON.toJSONString(evt));
        if (evt instanceof WebSocketClientProtocolHandler.ClientHandshakeStateEvent ) {
            WebSocketClientProtocolHandler.ClientHandshakeStateEvent event = (WebSocketClientProtocolHandler.ClientHandshakeStateEvent) evt;
            if(event.equals(HANDSHAKE_COMPLETE)){
                log.warn("sessionId={},与小爱链接userEventTriggered,evt={}",sessionId,JSON.toJSONString(evt));
                XiaoAiWebSocketDTO xiaoAiWebSocketDTO = XiaoAiService.XIAOAI_WEB_SOCKET_CLIENT_MAP.get(sessionId);
                xiaoAiWebSocketDTO.setHandshakeComplate(true);
                XiaoAiService.start(xiaoAiWebSocketDTO);
                sendMessageBySessionId(sessionId, 0,"establish with asr websocket success",WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_START);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

}
