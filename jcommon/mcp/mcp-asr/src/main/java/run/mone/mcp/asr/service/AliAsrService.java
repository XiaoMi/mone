package run.mone.mcp.asr.service;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberListener;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberResponse;
import com.google.gson.Gson;
import com.tencent.asrv2.AsrConstant;
import com.tencent.core.ws.SpeechClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author 龚文
 */
@Data
@Slf4j
public class AliAsrService {

    private static NlsClient client;
    private String appKey;
    private String id;
    private String secret;
    private String url;
    private Integer speechLength;
    private Long sleepTime;
    private SampleRateEnum sampleRate;
    private InputFormatEnum inputFormat;
    private String base64AudioFormat;


    private static Gson gson = new Gson();
    static SpeechClient proxy = new SpeechClient(AsrConstant.DEFAULT_RT_REQ_URL);
    private static final Map<String, InputFormatEnum> IMPUT_FORMAT_MAP = new HashMap<>();
    private static final Map<String, SampleRateEnum> SAMPLE_RATE_MAP = new HashMap<>();

    // 静态代码块，初始化输出格式和采样率的映射关系
    static {
        IMPUT_FORMAT_MAP.put(".pcm", InputFormatEnum.PCM);
        IMPUT_FORMAT_MAP.put(".wav", InputFormatEnum.WAV);

        SAMPLE_RATE_MAP.put("8000", SampleRateEnum.SAMPLE_RATE_8K);
        SAMPLE_RATE_MAP.put("16000", SampleRateEnum.SAMPLE_RATE_16K);
    }

    public AliAsrService(String appKey, String id, String secret, String url, String speechLength,
                         String sleepTime, String sampleRate) {
        this.appKey = appKey;
        this.id = id;
        this.secret = secret;
        this.url = url;
        this.speechLength = Integer.valueOf(speechLength);
        this.sleepTime = Long.valueOf(sleepTime);
        this.sampleRate = SAMPLE_RATE_MAP.getOrDefault(sampleRate, SampleRateEnum.SAMPLE_RATE_16K);
    }


    public Flux<String> doAsr(String fileName, String base64Audio) {
        return Flux.create(sink -> {
            sink.next("阿里语音识别结果为：");
            doAsr(fileName, base64Audio, sink);
            sink.complete();
        });
    }


    public void doAsr(String fileName, String base64Audio, FluxSink<String> sink) {
        String sessionId = UUID.randomUUID().toString();
        String suffixName = null;
        if (StringUtils.isNotBlank(fileName)) {
            suffixName = fileName.substring(fileName.lastIndexOf("."));
        } else if (StringUtils.isNotBlank(base64AudioFormat)) {
            suffixName = "." + base64AudioFormat;
        }
        inputFormat = IMPUT_FORMAT_MAP.getOrDefault(suffixName, InputFormatEnum.WAV);
        SpeechTranscriber transcriber = null;
        try {
            createAliNlsClient(id, secret, url);
            //创建实例、建立连接。
            transcriber = new SpeechTranscriber(client, getTranscriberListener(sessionId, sink));
            transcriber.setAppKey(appKey);
            //输入音频编码方式。
            if (".mp3".equals(suffixName)) {
                transcriber.addCustomedParam("format", "mp3");
            } else {
                transcriber.setFormat(inputFormat);
            }
            //输入音频采样率。
            transcriber.setSampleRate(sampleRate);
            //是否返回中间识别结果。
            transcriber.setEnableIntermediateResult(false);
            //是否生成并返回标点符号。
            transcriber.setEnablePunctuation(true);
            //是否将返回结果规整化，比如将一百返回为100。
            transcriber.setEnableITN(true);

            //设置vad断句参数。默认值：800ms，有效值：200ms～6000ms。
            //transcriber.addCustomedParam("max_sentence_silence", 600);
            //设置是否语义断句。
            //transcriber.addCustomedParam("enable_semantic_sentence_detection",false);
            //设置是否开启过滤语气词，即声音顺滑。
            //transcriber.addCustomedParam("disfluency",true);
            //设置是否开启词模式。
            //transcriber.addCustomedParam("enable_words",true);
            //设置vad噪音阈值参数，参数取值为-1～+1，如-0.9、-0.8、0.2、0.9。
            //取值越趋于-1，判定为语音的概率越大，亦即有可能更多噪声被当成语音被误识别。
            //取值越趋于+1，判定为噪音的越多，亦即有可能更多语音段被当成噪音被拒绝识别。
            //该参数属高级参数，调整需慎重和重点测试。
            //transcriber.addCustomedParam("speech_noise_threshold",0.3);
            //设置训练后的定制语言模型id。
            //transcriber.addCustomedParam("customization_id","你的定制语言模型id");
            //设置训练后的定制热词id。
            //transcriber.addCustomedParam("vocabulary_id","你的定制热词id");

            //此方法将以上参数设置序列化为JSON发送给服务端，并等待服务端确认。
            transcriber.start();

            //读文件
            if (StringUtils.isNotBlank(fileName)) {
                File file = new File(fileName);
                if (!file.exists()) {
                    throw new FileNotFoundException("File " + fileName + " Not Exit");
                }
                FileInputStream fis = new FileInputStream(file);
                byte[] b = new byte[speechLength];
                int len;
                while ((len = fis.read(b)) > 0) {
                    log.info("send data pack length: " + len);
                    transcriber.send(b, len);
                    //读取本地文件的形式模拟实时获取语音流并发送的，因为读取速度较快，这里需要设置sleep。
                    Thread.sleep(sleepTime);
                }
            } else if (StringUtils.isNotBlank(base64Audio)) {
                //base64编码的音频数据
                byte[] bytes = Base64.getDecoder().decode(base64Audio);
                List<byte[]> speechData = splitBytes(bytes, speechLength);
                for (byte[] speechDatum : speechData) {
                    transcriber.send(speechDatum);
                    Thread.sleep(sleepTime);
                }
            }
            //通知服务端语音数据发送完毕，等待服务端处理完成。
            transcriber.stop();
        } catch (Exception e) {
            log.error("do asr error ", e);
        } finally {
            if (null != transcriber) {
                transcriber.close();
            }
        }
    }


    public static List<byte[]> splitBytes(byte[] bytes, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }
        List<byte[]> result = new ArrayList<>();
        if (bytes == null || bytes.length == 0) {
            return result;
        }
        int totalLength = bytes.length;
        for (int i = 0; i < totalLength; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, totalLength);
            byte[] chunk = Arrays.copyOfRange(bytes, i, endIndex);
            result.add(chunk);
        }
        return result;
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


    private static SpeechTranscriberListener getTranscriberListener(String sessionId, FluxSink<String> sink) {

        return new SpeechTranscriberListener() {
            //识别出中间结果。仅当setEnableIntermediateResult为true时，才会返回该消息。
            @Override
            public void onTranscriptionResultChange(SpeechTranscriberResponse response) {
                log.info("{} sessionId:{},{}", "onTranscriptionResultChange", sessionId, new Gson().toJson(response));
            }

            @Override
            public void onTranscriberStart(SpeechTranscriberResponse response) {
                //task_id是调用方和服务端通信的唯一标识，遇到问题时，需要提供此task_id。
                log.info("{} sessionId:{},{}", "onTranscriberStart", sessionId, new Gson().toJson(response));

            }

            @Override
            public void onSentenceBegin(SpeechTranscriberResponse response) {
                log.info("{} sessionId:{},{}", "onSentenceBegin", sessionId, new Gson().toJson(response));

            }

            //识别出一句话。服务端会智能断句，当识别到一句话结束时会返回此消息。
            @Override
            public void onSentenceEnd(SpeechTranscriberResponse response) {
                log.info("{} sessionId:{},{}", "onSentenceEnd", sessionId, new Gson().toJson(response));
                sink.next(response.getTransSentenceText());
            }

            //识别完毕
            @Override
            public void onTranscriptionComplete(SpeechTranscriberResponse response) {
                log.info("{} sessionId:{},{}", "onTranscriptionComplete", sessionId, new Gson().toJson(response));

            }

            @Override
            public void onFail(SpeechTranscriberResponse response) {
                //task_id是调用方和服务端通信的唯一标识，遇到问题时，需要提供此task_id。
                log.info("{} sessionId:{},{}", "onFail", sessionId, new Gson().toJson(response));

            }
        };
    }


}
