package run.mone.mcp.tts.service;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.tts.StreamInputTts;
import com.alibaba.nls.client.protocol.tts.StreamInputTtsListener;
import com.alibaba.nls.client.protocol.tts.StreamInputTtsResponse;
import com.google.gson.Gson;
import com.tencent.core.utils.ByteUtils;
import com.tencent.tts.utils.Ttsutils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author 龚文
 */
@Data
@Slf4j
public class AliTtsService {

    private static NlsClient client;
    private String appKey;
    private String id;
    private String secret;
    private String url;
    private String voice;
    private OutputFormatEnum outputFormat;
    private SampleRateEnum sampleRate;

    private String isCreateAudioFile;
    private String isPlay;
    private String isOutputBase64;
    private byte[] audio;
    private static Gson gson = new Gson();
    private static String fileName;

    private static final Map<String, OutputFormatEnum> OUTPUT_FORMAT_MAP = new HashMap<>();
    private static final Map<String, SampleRateEnum> SAMPLE_RATE_MAP = new HashMap<>();

    // 静态代码块，初始化输出格式和采样率的映射关系
    static {
        OUTPUT_FORMAT_MAP.put("mp3", OutputFormatEnum.MP3);
        OUTPUT_FORMAT_MAP.put("pcm", OutputFormatEnum.PCM);
        OUTPUT_FORMAT_MAP.put("wav", OutputFormatEnum.WAV);
        OUTPUT_FORMAT_MAP.put("alaw", OutputFormatEnum.ALAW);
        OUTPUT_FORMAT_MAP.put("opu", OutputFormatEnum.OPU);

        SAMPLE_RATE_MAP.put("8000", SampleRateEnum.SAMPLE_RATE_8K);
        SAMPLE_RATE_MAP.put("16000", SampleRateEnum.SAMPLE_RATE_16K);
        SAMPLE_RATE_MAP.put("24000", SampleRateEnum.SAMPLE_RATE_24K);
        SAMPLE_RATE_MAP.put("48000", SampleRateEnum.SAMPLE_RATE_48K);
    }

    public AliTtsService(String appKey, String id, String secret, String url, String voice, String sampleRate,
                         String outputFormat) {
        this.appKey = appKey;
        this.id = id;
        this.secret = secret;
        this.url = url;
        this.voice = voice;
        this.sampleRate = SAMPLE_RATE_MAP.getOrDefault(sampleRate, SampleRateEnum.SAMPLE_RATE_16K);
        this.outputFormat = OUTPUT_FORMAT_MAP.getOrDefault(outputFormat, OutputFormatEnum.WAV);
    }

    public Flux<String> doTts(String text) {

        return Flux.create(sink -> {
            try {
                sink.next("阿里语音合成" + outputFormat + "音频二进制流结果为：");
                doTts(text, sink);
                sink.complete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void doTts(String textString, FluxSink<String> sink) throws InterruptedException {
        String sessionId = UUID.randomUUID().toString();
        fileName = "Ali-" + outputFormat.name() + "-" + sampleRate.name() + "-" + sessionId;
        audio = new byte[0];
        StreamInputTts synthesizer = null;

        PlaybackRunnable playbackRunnable = new PlaybackRunnable(sampleRate.value);
        Thread playbackThread = new Thread(playbackRunnable);

        if ("true".equals(isPlay) && !OutputFormatEnum.MP3.equals(outputFormat)) {
            try {
                playbackRunnable.prepare();
                log.info("Ali TTS play ,prepare");
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            // 启动播放线程
            playbackThread.start();
            log.info("Ali TTS play thread ,start");
        }

        try {
            log.info("Ali TTS connect ,appKey:" + appKey + ",id:" + id + ",secret:" + secret + ",url:" + url + "," +
                    "voice:" + voice + " ,outputFormat:" + outputFormat + " ,sampleRate:" + sampleRate + " ,sessionId" +
                    ":" + sessionId);
            createAliNlsClient(id, secret, url);
            //创建实例，建立连接。
            synthesizer = new StreamInputTts(client, getSynthesizerListener(sessionId, playbackRunnable, sink));
            synthesizer.setAppKey(appKey);
            //设置返回音频的编码格式。
            synthesizer.setFormat(outputFormat);
            //设置返回音频的采样率。
            synthesizer.setSampleRate(sampleRate);
            synthesizer.setVoice(voice);
            //此方法将以上参数设置序列化为JSON发送给服务端，并等待服务端确认。
            synthesizer.startStreamInputTts();
            synthesizer.sendStreamInputTts(textString);
            //通知服务端文本数据发送完毕，阻塞等待服务端处理完成。
            synthesizer.stopStreamInputTts();
            log.info("AliTts.audio.length:" + audio.length);
        } catch (Exception e) {
            log.error("Ali TTS error, sessionId={}", sessionId, e);
            throw new RuntimeException(e);
        } finally {
            if (synthesizer != null) {
                synthesizer.close();
            }
            playbackThread.join();
            log.info("Ali TTS closed");
        }
    }


    public static void createAliNlsClient(String accessKeyId, String accessKeySecret, String url) {
        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
            log.info("get token: " + accessToken.getToken() + ", expire time: " + accessToken.getExpireTime());
            if (url.isEmpty()) {
                client = new NlsClient(accessToken.getToken());
            } else {
                client = new NlsClient(url, accessToken.getToken());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StreamInputTtsListener getSynthesizerListener(String sessionId, PlaybackRunnable audioPlayer,
                                                          FluxSink<String> sink) {
        return new StreamInputTtsListener() {
            //流式文本语音合成开始
            @Override
            public void onSynthesisStart(StreamInputTtsResponse response) {
                log.info("Ali TTS synthesis start, sessionId={}, {}", sessionId, gson.toJson(response));
            }

            //服务端检测到了一句话的开始
            @Override
            public void onSentenceBegin(StreamInputTtsResponse response) {
                log.info("Ali TTS Sentence Begin, sessionId={}, {}", sessionId, gson.toJson(response));
            }

            //服务端检测到了一句话的结束，获得这句话的起止位置和所有时间戳
            @Override
            public void onSentenceEnd(StreamInputTtsResponse response) {
                log.info("Ali TTS Sentence End, sessionId={}, {}", sessionId, gson.toJson(response));
            }

            //流式文本语音合成结束
            @Override
            public void onSynthesisComplete(StreamInputTtsResponse response) {
                // 调用onSynthesisComplete时，表示所有TTS数据已经接收完成，所有文本都已经合成音频并返回。
                log.info("Ali TTS Synthesis Complete, sessionId={}, {}", sessionId, gson.toJson(response));

                if ("true".equals(isCreateAudioFile)) {
                    if ("pcm".equals(outputFormat.getName()) || "wav".equals(outputFormat.getName())) {
                        Ttsutils.responsePcm2Wav(sampleRate.value, audio, fileName);
                    }
                    if ("mp3".equals(outputFormat.getName())) {
                        Ttsutils.saveResponseToFile(audio, "./" + fileName + ".mp3");
                    }
                }
                if("true".equals(isOutputBase64)){
                    sink.next(Base64.getEncoder().encodeToString(audio));
                }else{
                    sink.next("0");
                }
                audioPlayer.stop();
                log.info("Ali Synthesis Complete, audio length:" + audio.length);
            }

            //收到语音合成的语音二进制数据
            @Override
            public void onAudioData(ByteBuffer message) {
                byte[] data = new byte[message.remaining()];
                message.get(data, 0, data.length);
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

            //收到语音合成的增量音频时间戳
            @Override
            public void onSentenceSynthesis(StreamInputTtsResponse response) {
            }

            @Override
            public void onFail(StreamInputTtsResponse response) {
                // task_id是调用方和服务端通信的唯一标识，当遇到问题时，需要提供此task_id以便排查。
                log.info(
                        "Ali TTS error : " +
                                "session_id: " + getStreamInputTts().getCurrentSessionId() +
                                ", task_id: " + response.getTaskId() +
                                //错误信息
                                ", status_text: " + response.getStatusText());
                audioPlayer.stop();
            }
        };

    }

}
