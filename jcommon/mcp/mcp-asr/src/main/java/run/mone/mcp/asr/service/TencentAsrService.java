package run.mone.mcp.asr.service;

import com.google.gson.Gson;
import com.tencent.asrv2.*;
import com.tencent.core.utils.ByteUtils;
import com.tencent.core.ws.Credential;
import com.tencent.core.ws.SpeechClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author 龚文
 */
@Data
@Slf4j
public class TencentAsrService {

    private String appId;
    private String secretId;
    private String secretKey;
    private String speechLength;
    private Long sleepTime;
    private String engineModelType;
    //1：pcm；4：speex(sp)；6：silk；8：mp3；10：opus；12：wav；14：m4a（
    private String voiceFormat;
    //构造voiceFormat映射
    private static Map<String, String> VOICE_FORMAT_MAP = new HashMap<>();

    static {
        VOICE_FORMAT_MAP.put(".pcm", "1");
        VOICE_FORMAT_MAP.put(".speex", "4");
        VOICE_FORMAT_MAP.put(".silk", "6");
        VOICE_FORMAT_MAP.put(".mp3", "8");
        VOICE_FORMAT_MAP.put(".opus", "10");
        VOICE_FORMAT_MAP.put(".wav", "12");
        VOICE_FORMAT_MAP.put(".m4a", "14");
    }

    private static Gson gson = new Gson();
    static SpeechClient proxy = new SpeechClient(AsrConstant.DEFAULT_RT_REQ_URL);


    public TencentAsrService(String appId, String secretId, String secretKey, String speechLength, String sleepTime,
                             String engineModelType) {
        this.appId = appId;
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.speechLength = speechLength;
        this.sleepTime = Long.valueOf(sleepTime);
        this.engineModelType = engineModelType;
    }


    public Flux<String> doAsr(String fileName) {
        return Flux.create(sink -> {
            sink.next("腾讯语音识别结果为：");
            doAsr(fileName, sink);
            sink.complete();
        });
    }

    public void doAsr(String fileName, FluxSink<String> sink) {
        //获取filename后缀
        String sessionId = UUID.randomUUID().toString();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        voiceFormat = VOICE_FORMAT_MAP.getOrDefault(suffixName, "12");

        SpeechRecognizerRequest request = SpeechRecognizerRequest.init();
        request.setEngineModelType(engineModelType);
        request.setVoiceFormat(Integer.valueOf(voiceFormat));
        request.setNeedVad(1);
        request.setVoiceId(sessionId);

        SpeechRecognizer speechRecognizer = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            List<byte[]> speechData = ByteUtils.subToSmallBytes(fileInputStream, Integer.parseInt(speechLength));
            Credential credential = new Credential(appId, secretId, secretKey);
            speechRecognizer = new SpeechRecognizer(proxy, credential, request, getSpeechRecognizerListener(sessionId
                    , sink));
            speechRecognizer.start();
            for (byte[] speechDatum : speechData) {
                Thread.sleep(sleepTime);
                //发送数据
                speechRecognizer.write(speechDatum);
            }
            speechRecognizer.stop();
        } catch (Exception e) {
            log.error("do asr error ", e);
        } finally {
            if (speechRecognizer != null) {
                speechRecognizer.close(); //关闭连接
            }
        }
    }


    private SpeechRecognizerListener getSpeechRecognizerListener(String sessionId, FluxSink<String> sink) {
        return new SpeechRecognizerListener() {
            @Override
            public void onRecognitionStart(SpeechRecognizerResponse response) {//首包回调
                log.info("{} sessionId:{},{}", "onRecognitionStart", sessionId, new Gson().toJson(response));
            }

            @Override
            public void onSentenceBegin(SpeechRecognizerResponse response) {//一段话开始识别 slice_type=0
                log.info("{} sessionId:{},{}", "onSentenceBegin", sessionId, new Gson().toJson(response));
            }

            @Override
            public void onRecognitionResultChange(SpeechRecognizerResponse response) {//一段话识别中，slice_type=1,
                // voice_text_str 为非稳态结果(该段识别结果还可能变化)
//                log.info("{} sessionId:{},{}", "onRecognitionResultChange", sessionId, new Gson().toJson(response));
            }

            @Override
            public void onSentenceEnd(SpeechRecognizerResponse response) {//一段话识别结束，slice_type=2,voice_text_str 为稳态结果
                // (该段识别结果不再变化)
                log.info("{} sessionId:{},{}", "onSentenceEnd", sessionId, new Gson().toJson(response));
                sink.next(response.getResult().getVoiceTextStr());
            }

            @Override
            public void onRecognitionComplete(SpeechRecognizerResponse response) {//识别完成回调 即final=1
                log.info("{} sessionId:{},{}", "onRecognitionComplete", sessionId, new Gson().toJson(response));
            }

            @Override
            public void onFail(SpeechRecognizerResponse response) {//失败回调
                log.info("{} sessionId:{},{}", "onFail", sessionId, new Gson().toJson(response));
            }

            @Override
            public void onMessage(SpeechRecognizerResponse response) {//所有消息都会回调该方法
//                log.info("{} sessionId:{},{}", "onMessage", sessionId, new Gson().toJson(response));
            }
        };
    }


}
