package run.mone.m78.service.service.multiModal.audio;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import run.mone.ai.bytedance.ArsClientOut;
import run.mone.ai.bytedance.ArsRequest;
import run.mone.ai.minimax.MiniMax;
import run.mone.ai.minimax.bo.RequestBodyContent;
import run.mone.ai.minimax.bo.T2AProResponse;
import run.mone.m78.api.bo.multiModal.audio.AudioParam;
import run.mone.m78.service.bo.user.TextToSpeechReq;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.service.base.ChatgptService;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static run.mone.m78.api.constant.CommonConstant.TMP_DIR;

/**
 * @author wmin
 * @date 2024/1/16
 */
@Service
@Slf4j
public class AudioService {

    @Value("${minimax.groupId:}")
    private String minimaxGroupId;

    @Value("${minimax.authorization:}")
    private String minimaxAuthorization;

    @Value("${bytedance.appId:}")
    private String bytedanceAppId;

    @Value("${bytedance.token:}")
    private String bytedanceToken;

    @Value("${bytedance.cluster:}")
    private String bytedanceCluster;


    @Resource
    private ChatgptService chatgptService;

    /**
     * 将音频文件转换为文本
     *
     * @param audioFile 音频文件
     * @return 转换后的文本，如果发生错误则返回null
     */
	public String audioToText(MultipartFile audioFile){
        try {
            Path filePath = Paths.get(TMP_DIR + "/" + UUIDUtil.generateType1UUID() + audioFile.getOriginalFilename());
            Files.write(filePath, audioFile.getBytes());
            return chatgptService.audioToText(filePath.toString());
        } catch (Exception e){
            log.error("audioToText error", e);
            return null;
        }
    }

    /**
     * 将文本转换为音频
     *
     * @param audioParam 包含音频参数的对象
     * @return 转换后的音频字节数组
     */
	public byte[] textToAudio(AudioParam audioParam){
        return chatgptService.textToAudio(audioParam);
    }

    /**
     * 将文本转换为语音
     *
     * @param textToSpeechReq 包含文本和语音ID的请求对象
     * @return 生成的语音字节数组，如果发生异常则返回null
     */
	public byte[] textToSpeech(TextToSpeechReq textToSpeechReq){
        try {
            RequestBodyContent requestBodyContent = new RequestBodyContent();
            requestBodyContent.setText(textToSpeechReq.getText());
            if (StringUtils.isNotEmpty(textToSpeechReq.getVoiceId())) {
                requestBodyContent.setVoice_id(textToSpeechReq.getVoiceId());
            }
            return MiniMax.call_Text_To_Speech(minimaxGroupId, minimaxAuthorization, requestBodyContent);
        } catch (Exception e){
            log.error("textToSpeech error", e);
            return null;
        }
    }

    /**
     * 将文本转换为语音
     *
     * @param textToSpeechReq 包含文本和语音ID的请求对象
     * @return T2AProResponse 转换后的语音响应对象，如果发生异常则返回null
     */
	public T2AProResponse T2APro(TextToSpeechReq textToSpeechReq) {
        try {
            RequestBodyContent requestBodyContent = new RequestBodyContent();
            requestBodyContent.setText(textToSpeechReq.getText());
            if (StringUtils.isNotEmpty(textToSpeechReq.getVoiceId())) {
                requestBodyContent.setVoice_id(textToSpeechReq.getVoiceId());
            }
            return MiniMax.call_T2A_Pro(minimaxGroupId, minimaxAuthorization, requestBodyContent);
        } catch (Exception e) {
            log.error("T2APro error", e);
            return null;
        }
    }

    /**
     * 调用ArsClient服务，发送音频数据并获取响应
     *
     * @param bytes 音频数据的字节数组
     * @param format 音频格式，如果为空则默认为 "wav"
     * @return 服务响应的字符串，如果发生异常则返回 null
     */
	public String callArsClient(byte[] bytes, String format) {
        try {
            ArsRequest request = new ArsRequest();
            request.setAppId(bytedanceAppId);  // 项目的 appid
            request.setToken(bytedanceToken);  // 项目的 token
            request.setCluster(bytedanceCluster); // 请求的集群
            if (StringUtils.isEmpty(format)) {
                request.setAudio_format("wav");
            } else {
                request.setAudio_format(format);
            }
            request.setAudio(bytes);
            String response = ArsClientOut.callArsClient(request);
            return response;
        } catch (Exception e) {
            log.error("callArsClient error", e);
            return null;
        }
    }


}
