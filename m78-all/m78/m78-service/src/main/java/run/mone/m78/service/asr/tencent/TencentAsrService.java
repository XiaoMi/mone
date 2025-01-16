package run.mone.m78.service.asr.tencent;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencent.asrv2.*;
import com.tencent.core.utils.ByteUtils;
import com.tencent.core.ws.Credential;
import com.tencent.core.ws.SpeechClient;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.stringtemplate.v4.ST;
import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedError;
import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedRes;
import run.mone.m78.common.URIParser;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.ws.WsSessionHolder;
import run.mone.m78.common.Constant;
import run.mone.m78.service.asr.AsrResponse;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.exceptions.ExCodes;
import run.mone.m78.service.service.multiModal.AudioAsrCostService;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
@Service

/**
 * TencentAsrService类提供了与腾讯语音识别服务进行WebSocket连接的功能。
 * 该类负责管理WebSocket会话，处理音频数据的传输和识别结果的接收。
 *
 * 主要功能包括：
 * - 建立和管理与腾讯语音识别服务的WebSocket连接。
 * - 处理来自客户端的二进制和文本消息，并将其发送到腾讯语音识别服务。
 * - 接收和处理腾讯语音识别服务的识别结果和状态更新。
 * - 管理WebSocket会话的生命周期，包括连接的建立、关闭和清理。
 *
 * 该类还包含一个内部静态类TencentAsrListener，用于处理腾讯语音识别服务的回调事件。
 *
 * 使用了@Slf4j注解进行日志记录，使用了@Data注解生成常用的getter和setter方法，
 * 使用了@Service注解标识为Spring服务组件。
 */

public class TencentAsrService {

    public static ConcurrentHashMap<String, SpeechRecognizer> TENCENT_WEB_SOCKET_SESSION_MAP = new ConcurrentHashMap<>();

    private static SpeechClient Tencent_Speech_Proxy = new SpeechClient(AsrConstant.DEFAULT_RT_REQ_URL);

    @Resource
    private TencentAsrBaseService asrBaseService;

    @Resource
    private AudioAsrCostService audioAsrCostService;


    public void doConnect(WebSocketSession session) {
        String sessionId = session.getId();
        URI uri = session.getUri();
        String fromUri = URIParser.getQueryParamValue(uri, TencentAsrBaseService.FROM);

        // check from字段是否合法
        String account = asrBaseService.getAccountByFrom(fromUri);
        if (account == null || account.isEmpty()) {
            // 不合法的from，断开链接
            AsrResponse.sendMessageBySessionId(sessionId, AsrRecognizedError.ASR_ESTABLISH_FAILED.getCode(),
                    "establish with tencent asr websocket failed, the from param have error",
                    WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_END);
            throw new RuntimeException("the from param have error");
        }

        String recognizedVoiceId = UUID.randomUUID().toString();

        try {
            // init listener with sessionId
            TencentAsrService.TencentAsrListener listener = new TencentAsrService.TencentAsrListener(sessionId, fromUri, audioAsrCostService);

            Credential credential = new Credential(
                    asrBaseService.getAppId(account),
                    asrBaseService.getSecretId(account),
                    asrBaseService.getSecretKey(account));
            SpeechRecognizerRequest request = SpeechRecognizerRequest.init();

            // 设置engine
            request.setEngineModelType(asrBaseService.getModelEngine(uri));
            // 设置编码格式
            request.setVoiceFormat(asrBaseService.getFormat(uri));

            request.setNeedVad(1);
            //voice_id为请求标识，需要保持全局唯一（推荐使用 uuid），遇到问题需要提供该值方便服务端排查
            request.setVoiceId(recognizedVoiceId);

            SpeechRecognizer speechRecognizer = new SpeechRecognizer(Tencent_Speech_Proxy, credential, request, listener);
            speechRecognizer.start();
            TENCENT_WEB_SOCKET_SESSION_MAP.put(sessionId, speechRecognizer);
        } catch (Exception e) {
            AsrResponse.sendMessageBySessionId(sessionId, AsrRecognizedError.ASR_ESTABLISH_FAILED.getCode(),
                    "establish with asr websocket failed",
                    WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_END);
            log.error("tencent speechRecognizer start failed, ", e);
            throw new RuntimeException(e);
        }

        AsrResponse.sendMessageBySessionId(sessionId, 0,
                "establish with asr websocket success",
                WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_START);

    }

    // 断开和tencent的长连接, 清理长连接
    public void closeAndClearTencentWs(String sessionId) {
        if (TENCENT_WEB_SOCKET_SESSION_MAP.get(sessionId) != null) {
            // 可以多次close
            TENCENT_WEB_SOCKET_SESSION_MAP.get(sessionId).close();
            TENCENT_WEB_SOCKET_SESSION_MAP.remove(sessionId);
        }
    }

    // 主动断开和上游客户端的长连接
    // note: m78主动断开和客服的长连接，会调用到外层的afterConnectionClosed，
    // 最终会调用都上面的closeAndClearTencentWs，清理TENCENT_WEB_SOCKET_SESSION_MAP
    // note: voiceId有存在传输为空得情况
    public static void closeClientWs(String sessionId, String voiceId) {
        try {
            WsSessionHolder.INSTANCE.getBySessionId(sessionId).close(CloseStatus.NORMAL);
        } catch (IOException e) {
            log.error("tencent ws closed but close ws with client failed, session_id:{} voice_id:{}", sessionId, voiceId);
        }
    }

    public void writeBinaryMessage(BinaryMessage message, String sessionId) {
        ByteBuffer payload = message.getPayload();

        byte[] audioData = new byte[payload.remaining()];
        payload.get(audioData);
        log.info("get binaray message session_id: {}, length: {}", sessionId, audioData.length);

        try {
            TENCENT_WEB_SOCKET_SESSION_MAP.get(sessionId).write(audioData);
        } catch (Exception e) {
            log.error("handle binary message Error: {}", e.getMessage());
            AsrResponse.sendMessageBySessionId(sessionId, AsrRecognizedError.ASR_RECOGNIZED_ERROR.getCode(), "send binary failed", WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_END);
            // 关闭和上游得ws
            // 发送binary没有voice_id，只有session_id
            closeClientWs(sessionId, "");
        }
    }


    public void writeTextMessage(TextMessage message, String sessionId) {
        try {
            log.info("get text message session_id: {}, payload: {}", sessionId, message.getPayload());
            JsonObject jsonObject = new Gson().fromJson(message.getPayload(), JsonObject.class);
            // 处理base64
            if (jsonObject.has("type") && jsonObject.get("type").getAsString().equals(WebsocketMessageType.MULTI_MODAL_AUDIO_BASE64)) {
                String base64 = jsonObject.get("data").getAsString();
                byte[] audioStream = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
                TENCENT_WEB_SOCKET_SESSION_MAP.get(sessionId).write(audioStream);
            }
            // 只处理type=END消息
            if (jsonObject.has("type") && jsonObject.get("type").getAsString().equals(WebsocketMessageType.MULTI_MODAL_AUDIO_END)) {
                // 给腾讯发送{"type": "end"}结束标识
                TENCENT_WEB_SOCKET_SESSION_MAP.get(sessionId).stop();
            }
        } catch (Exception e) {
            log.error("Tencent asr websocket session_id={} stop failed: {}", sessionId, e.getMessage());
        }
    }


    @Setter
    public static class TencentAsrListener extends SpeechRecognizerListener {

        // m78 websocket的sessionId
        private String sessionId;
        // 业务线
        private String productLine;
        // 通话时长,单位毫秒
        private Long durationOfCall;

        private AudioAsrCostService audioAsrCostService;


        public TencentAsrListener(String sessionId, String productLine, AudioAsrCostService audioAsrCostService) {
            this.sessionId = sessionId;
            this.productLine = productLine;
            this.audioAsrCostService = audioAsrCostService;
        }

        public String getNow() {
            // 获取当前时间戳，精确到毫秒
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            return now.format(formatter);
        }

        @Override
        public void onRecognitionStart(SpeechRecognizerResponse response) {
            //首包回调
            log.info("tencent asr onRecognitionStart,time:{} session_id:{} voice_id:{},{}", getNow(), sessionId, response.getVoiceId(), new Gson().toJson(response));
        }

        @Override
        public void onSentenceBegin(SpeechRecognizerResponse response) {
            //一段话开始识别 slice_type=0
            // nothing
        }

        @Override
        public void onRecognitionResultChange(SpeechRecognizerResponse response) {
            //一段话识别中，slice_type=1,voice_text_str 为非稳态结果(该段识别结果还可能变化)
            //log.info("Tencent asr onRecognitionResultChange,time:{} session_id:{} voice_id:{},{}", getNow(), sessionId,response.getVoiceId(), new Gson().toJson(response));
            AsrRecognizedRes.AsrRecognizedData data = AsrRecognizedRes.AsrRecognizedData.builder()
                    .startTime(response.getResult().getStartTime())
                    .endTime(response.getResult().getEndTime())
                    .text(response.getResult().getVoiceTextStr())
                    .isFinal(false)
                    .voiceId(response.getVoiceId())
                    .build();
            AsrResponse.sendRecognizedDataBySessionId(sessionId, data);
        }

        @Override
        public void onSentenceEnd(SpeechRecognizerResponse response) {
            //一段话识别结束，slice_type=2,voice_text_str 为稳态结果(该段识别结果不再变化)
            log.info("Tencent asr onSentenceEnd,time:{} session_id:{} voice_id:{}, {}", getNow(), sessionId, response.getVoiceId(), new Gson().toJson(response));
            // 更新识别的时长
            durationOfCall = response.getResult().getEndTime();
            AsrRecognizedRes.AsrRecognizedData data = AsrRecognizedRes.AsrRecognizedData.builder()
                    .startTime(response.getResult().getStartTime())
                    .endTime(response.getResult().getEndTime())
                    .text(response.getResult().getVoiceTextStr())
                    .isFinal(true)
                    .voiceId(response.getVoiceId())
                    .build();
            AsrResponse.sendRecognizedDataBySessionId(sessionId, data);
        }

        @Override
        public void onRecognitionComplete(SpeechRecognizerResponse response) {
            //识别完成回调 即final=1, asr侧主动断开
            log.info("Tencent asr RecognitionComplete, time:{} session_id:{}, voice_id:{}", getNow(), sessionId, response.getVoiceId());
            // 更新db通话时长
            audioAsrCostService.saveOrUpdateUsedTime(Constant.TENCENT_PLATFORM, productLine, durationOfCall);

            AsrResponse.sendMessageBySessionId(sessionId, 0, "end", WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_END);

            // 和客户端断开，并清理腾讯ws
            closeClientWs(sessionId, response.getVoiceId());
        }

        @Override
        public void onFail(SpeechRecognizerResponse response) {
            //失败回调，asr侧主动断开
            log.error("Tencent asr onFail session_id:{}, voice_id: {},code: {} err: {}", sessionId, response.getVoiceId(), response.getCode(), new Gson().toJson(response));

            // todo:增加监控报警
            AsrResponse.sendMessageBySessionId(sessionId, AsrRecognizedError.ASR_RECOGNIZED_ERROR.getCode(), response.getMessage(), WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_END);

            // 更新db通话时长
            audioAsrCostService.saveOrUpdateUsedTime(Constant.TENCENT_PLATFORM, productLine, durationOfCall);

            // 和客户端断开，并清理腾讯ws
            closeClientWs(sessionId, response.getVoiceId());
        }

        @Override
        public void onMessage(SpeechRecognizerResponse response) {
            //所有消息都会回调该方法
            // nothing
            //log.warn("{} session_id:{}, voice_id: {},err: {}", "Tencent asr onMessage", sessionId, response.getVoiceId(), new Gson().toJson(response));
        }
    }
}
