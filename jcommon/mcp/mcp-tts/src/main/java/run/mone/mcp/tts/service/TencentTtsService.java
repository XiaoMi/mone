package run.mone.mcp.tts.service;

import com.google.gson.Gson;
import com.tencent.core.utils.ByteUtils;
import com.tencent.core.ws.Credential;
import com.tencent.core.ws.SpeechClient;
import com.tencent.tts.utils.Ttsutils;
import com.tencent.ttsv2.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.sound.sampled.LineUnavailableException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**
 * @author 龚文
 */
@Data
@Slf4j
public class TencentTtsService {
    private String appId;
    private String secretId;
    private String secretKey;
    private String codec;
    private Integer sampleRate;
    private Integer voiceType;

    private String isCreateAudioFile;
    private String isPlay;
    private String isOutputBase64;
    private byte[] audio;
    private static Gson gson = new Gson();
    private static String fileName;
    static SpeechClient proxy = new SpeechClient(TtsConstant.DEFAULT_TTS_V2_REQ_URL);

    public TencentTtsService(String appId, String secretId, String secretKey, String voiceType, String sampleRate,
                             String codec) {
        this.appId = appId;
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.voiceType = Integer.valueOf(voiceType);
        this.sampleRate = Integer.valueOf(sampleRate);
        this.codec = codec;
    }

    public Flux<String> doTts(String textString) {

        return Flux.create(sink -> {
            try {
                sink.next("腾讯语音合成" + codec + "音频数据流结果为：");
                doTts(textString, sink);
                sink.complete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void doTts(String textString, FluxSink<String> sink) throws InterruptedException {
        String sessionId = UUID.randomUUID().toString();
        fileName = "Tencent-" + codec + "-" + sampleRate + "-" + sessionId;
        audio = new byte[0];
        SpeechSynthesizer synthesizer = null;

        PlaybackRunnable playbackRunnable = new PlaybackRunnable(sampleRate);
        Thread playbackThread = new Thread(playbackRunnable);

        if ("true".equals(isPlay) && !"mp3".equals(codec)) {
            try {
                playbackRunnable.prepare();
                log.info("Tencent TTS play ,prepare");
            } catch (LineUnavailableException e) {
                log.error("Tencent TTS play ,prepare error", e);
                throw new RuntimeException(e);
            }
            // 启动播放线程
            playbackThread.start();
            log.info("Tencent TTS play thread ,start");
        }

        try {
            log.info("Tencent TTS connect ,appId:" + appId + " ,secretId:" + secretId + " ,secretKey:" + secretId
                    + " ,codec:" + codec + " ,sampleRate:" + sampleRate + " ,voiceType:" +
                    voiceType + " ,sessionId:" + sessionId);

            Credential credential = new Credential(appId, secretId, secretKey);
            SpeechSynthesizerRequest request = new SpeechSynthesizerRequest();
            request.setSessionId(sessionId);//sessionId，需要保持全局唯一（推荐使用 uuid），遇到问题需要提供该值方便服务端排查
            request.setCodec(codec);
            request.setSampleRate(sampleRate);
            request.setVoiceType(voiceType);
            request.setText(textString);
            //synthesizer不可重复使用，每次合成需要重新生成新对象
            synthesizer = new SpeechSynthesizer(proxy, credential, request,
                    getSynthesizerListener(sessionId, playbackRunnable, sink));
            synthesizer.start();
            synthesizer.stop();
            log.info("TencentTts.audio.length:" + audio.length);
        } catch (Exception e) {
            log.error("Tencent TTS error, sessionId={}", sessionId, e);
            throw new RuntimeException(e);
        } finally {
            if (synthesizer != null) {
                synthesizer.close(); //关闭连接
                log.info("Tencent TTS closed");
            }
            playbackThread.join();
        }
    }


    private SpeechSynthesizerListener getSynthesizerListener(String sessionId,
                                                             PlaybackRunnable audioPlayer, FluxSink<String> sink) {
        return new SpeechSynthesizerListener() {
            @Override
            public void onSynthesisStart(SpeechSynthesizerResponse response) {
                log.info("{} session_id:{},{}", "onSynthesisStart", sessionId, gson.toJson(response));
            }

            @Override
            public void onSynthesisEnd(SpeechSynthesizerResponse response) {
                log.info("{} session_id:{},{}", "onSynthesisEnd", sessionId, gson.toJson(response));
                if ("true".equals(isCreateAudioFile)) {
                    if ("pcm".equals(codec)) {
                        Ttsutils.responsePcm2Wav(sampleRate, audio, fileName);
                    }
                    if ("mp3".equals(codec)) {
                        Ttsutils.saveResponseToFile(audio, "./" + fileName + ".mp3");
                    }
                }

                if("true".equals(isOutputBase64)){
                        sink.next("hiveVoiceBase64-" + Base64.getEncoder().encodeToString(audio));
                }else{
                    sink.next("0");
                }
                audioPlayer.stop();
                log.info("tencentTts onSynthesisEnd, audio length:" + audio.length);
            }

            @Override
            public void onAudioResult(ByteBuffer buffer) {
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                if (data.length <= 1) {
                    return;
                }
                //若data不是偶数，则在末尾补一个0
                if (data.length % 2 != 0) {
                    byte[] newData = new byte[data.length + 1];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    newData[newData.length - 1] = 0;
                    data = newData;
                }
                audio = ByteUtils.concat(audio, data);
                audioPlayer.put(ByteBuffer.wrap(data));
                if("false".equals(isOutputBase64)){
                    sink.next(Arrays.toString(data).replace("[", "").replace("]", ", "));
                }
            }

            @Override
            public void onTextResult(SpeechSynthesizerResponse response) {
                log.info("{} session_id:{},{}", "onTextResult", sessionId, gson.toJson(response));
            }

            @Override
            public void onSynthesisFail(SpeechSynthesizerResponse response) {
                log.info("{} session_id:{},{}", "onSynthesisFail", sessionId, gson.toJson(response));
                audioPlayer.stop();
            }
        };
    }


}
